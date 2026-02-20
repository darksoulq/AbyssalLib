const app = {
    token: null,
    registry: { permissions: [], custom: [], groups: [] },
    data: { groups: [], users: [] },
    current: { type: null, id: null },
    contextTarget: null,
    sidebarContextTarget: null,

    init() {
        const params = new URLSearchParams(window.location.search);
        this.token = params.get('token') || sessionStorage.getItem('token');
        if (params.get('token')) {
            sessionStorage.setItem('token', this.token);
            window.history.replaceState({}, document.title, "/");
        }

        if (!this.token) {
            document.getElementById('auth-layer').style.display = 'flex';
        } else {
            document.getElementById('auth-layer').style.display = 'none';
            document.getElementById('app').style.display = 'flex';
            this.loadAll();

            document.addEventListener('click', (e) => {
                if (!e.target.closest('.input-wrapper')) {
                    document.getElementById('node-suggestions').style.display = 'none';
                }
                document.getElementById('context-menu').style.display = 'none';
                document.getElementById('sidebar-context-menu').style.display = 'none';
            });

            document.getElementById('add-node-input').addEventListener('focus', (e) => {
                this.updateSuggestions(e.target.value);
            });

            setInterval(() => {
                let changed = false;
                document.querySelectorAll('[data-expiry]').forEach(el => {
                    const exp = parseInt(el.getAttribute('data-expiry'));
                    if (exp > 0) {
                        const prefix = el.getAttribute('data-prefix') || '';
                        const timeStr = app.formatTime(exp);
                        if (timeStr === "Expired" && el.innerText !== prefix + "Expired") changed = true;
                        el.innerText = prefix + timeStr;
                    }
                });
                if (changed && app.current.type) {
                    app.refreshData();
                }
            }, 1000);
        }
    },

    showToast(msg, type = 'success') {
        const container = document.getElementById('toast-container');
        const toast = document.createElement('div');
        toast.className = `toast ${type}`;
        toast.innerText = msg;
        container.appendChild(toast);
        setTimeout(() => toast.remove(), 3000);
    },

    async loadAll() {
        try {
            const regRes = await fetch(`/api/registry?token=${this.token}`);
            if (!regRes.ok) throw new Error();
            const rawReg = await regRes.json();
            this.registry = {
                permissions: rawReg.permissions || [],
                custom: rawReg.custom_permissions || [],
                groups: rawReg.groups || []
            };
            this.registry.permissions.sort((a,b) => this.compareNodes(a.node, b.node));
            this.updateGroupDatalist();
            await this.refreshData();
        } catch(e) {
            this.showToast("Connection lost or session expired.", 'error');
            document.querySelector('.status-indicator').innerHTML = '<div class="status-dot" style="background:var(--danger);"></div> Disconnected';
        }
    },

    async refreshData() {
        const res = await fetch(`/api/data?token=${this.token}`);
        if (!res.ok) throw new Error();
        this.data = await res.json();
        this.renderSidebar();
        if (this.current.type) this.renderEditor();
    },

    async action(payload) {
        try {
            const res = await fetch(`/api/action?token=${this.token}`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });
            if (!res.ok) throw new Error();
            if (payload.action === 'endSession') {
                sessionStorage.removeItem('token');
                window.location.reload();
                return;
            }
            await this.refreshData();
            if (payload.action !== 'deleteGroup') this.showToast("Saved successfully");
        } catch(e) {
            this.showToast("Action failed.", 'error');
        }
    },

    formatTime(expiry) {
        if (!expiry || expiry === 0) return "Permanent";
        const diff = expiry - Date.now();
        if (diff <= 0) return "Expired";

        const s = Math.floor(diff / 1000);
        if (s < 60) return s + "s";
        const m = Math.floor(s / 60);
        if (m < 60) return m + "m";
        const h = Math.floor(m / 60);
        if (h < 24) return h + "h";
        return Math.floor(h / 24) + "d";
    },

    parseDuration(str) {
        if (!str || str.toLowerCase() === 'permanent' || str === '0') return 0;
        let total = 0;
        const regex = /(\d+)\s*(y|mo|w|d|h|m|s)/g;
        let match;
        while ((match = regex.exec(str.toLowerCase())) !== null) {
            const val = parseInt(match[1]);
            const unit = match[2];
            if (unit === 'y') total += val * 31536000000;
            else if (unit === 'mo') total += val * 2592000000;
            else if (unit === 'w') total += val * 604800000;
            else if (unit === 'd') total += val * 86400000;
            else if (unit === 'h') total += val * 3600000;
            else if (unit === 'm') total += val * 60000;
            else if (unit === 's') total += val * 1000;
        }
        return total > 0 ? Date.now() + total : 0;
    },

    compareNodes(a, b) {
        const p1 = a.split('.');
        const p2 = b.split('.');
        for (let i = 0; i < Math.min(p1.length, p2.length); i++) {
            const c = p1[i].localeCompare(p2[i]);
            if (c !== 0) return c;
        }
        return p1.length - p2.length;
    },

    selectItem(type, id) {
        this.current = { type, id };
        document.querySelectorAll('.tree-item').forEach(el => el.classList.remove('active'));
        const el = document.getElementById(`nav-${type}-${id}`);
        if (el) el.classList.add('active');
        this.renderEditor();

        if (window.innerWidth <= 768) {
            document.querySelector('.sidebar').classList.add('mobile-hidden');
            document.querySelector('.main-content').classList.add('mobile-active');
        }
    },

    backToSidebar() {
        document.querySelector('.sidebar').classList.remove('mobile-hidden');
        document.querySelector('.main-content').classList.remove('mobile-active');
    },

    filterSidebar() {
        const query = document.getElementById('tree-search').value.toLowerCase();
        document.querySelectorAll('.tree-item').forEach(el => {
            const text = el.innerText.toLowerCase();
            el.style.display = text.includes(query) ? 'flex' : 'none';
        });
    },

    renderSidebar() {
        const gList = document.getElementById('tree-groups');
        const sortedGroups = [...this.data.groups].sort((a,b) => a.id.localeCompare(b.id));
        gList.innerHTML = sortedGroups.map(g => `
            <div class="tree-item ${this.current.type === 'group' && this.current.id === g.id ? 'active' : ''}" id="nav-group-${g.id}" onclick="app.selectItem('group', '${g.id}')" oncontextmenu="app.showSidebarContext(event, 'group', '${g.id}', ${g.weight})">
                <span>${g.id}</span>
            </div>
        `).join('');

        const uList = document.getElementById('tree-users');
        const sortedUsers = [...this.data.users].sort((a,b) => a.name.localeCompare(b.name));
        uList.innerHTML = sortedUsers.map(u => `
            <div class="tree-item ${this.current.type === 'user' && this.current.id === u.uuid ? 'active' : ''}" id="nav-user-${u.uuid}" onclick="app.selectItem('user', '${u.uuid}')">
                <span>${u.name}</span>
                <div class="status-dot ${u.online ? 'online' : ''}" title="${u.online ? 'Online' : 'Offline'}"></div>
            </div>
        `).join('');
    },

    renderEditor() {
        document.getElementById('empty-state').style.display = 'none';
        document.getElementById('editor-area').style.display = 'flex';

        const isGroup = this.current.type === 'group';
        const entity = isGroup
            ? this.data.groups.find(g => g.id === this.current.id)
            : this.data.users.find(u => u.uuid === this.current.id);

        if (!entity) {
            document.getElementById('editor-area').style.display = 'none';
            document.getElementById('empty-state').style.display = 'flex';
            return;
        }

        if (entity.lazy) {
            this.action({ action: 'loadUser', uuid: entity.uuid });
            return;
        }

        let backBtn = window.innerWidth <= 768 ? `<button class="icon-btn" onclick="app.backToSidebar()" style="margin-right:10px;"><i class="fa-solid fa-arrow-left"></i></button>` : '';

        document.getElementById('editor-path').innerHTML = `Permissions / <span>${isGroup ? 'Groups' : 'Users'}</span> / <span>${isGroup ? entity.id : entity.name}</span>`;
        document.getElementById('editor-icon').className = isGroup ? 'fa-solid fa-users title-icon' : 'fa-solid fa-user title-icon';
        document.getElementById('editor-name').innerHTML = `${backBtn}${isGroup ? entity.id : entity.name}`;
        document.getElementById('editor-subtitle').innerText = isGroup ? `group` : `uuid: ${entity.uuid}`;

        let actionsHtml = '';
        if (isGroup) {
            actionsHtml += `
                <div class="weight-box">
                    <span style="font-size:0.8rem; color:var(--text-muted); font-weight:bold;">WEIGHT</span>
                    <input type="number" class="weight-input" value="${entity.weight}" onchange="app.action({action:'setGroupWeight', id:'${entity.id}', weight: parseInt(this.value)})">
                </div>
            `;
        }
        document.getElementById('editor-actions').innerHTML = actionsHtml;

        const parentsList = document.getElementById('parents-list');
        if (entity.parents.length === 0) {
            parentsList.innerHTML = '<span style="color:var(--text-muted); font-size:0.85rem; font-style:italic;">No parents assigned</span>';
        } else {
            const sortedParents = [...entity.parents].sort((a,b) => this.compareNodes(a.key, b.key));
            parentsList.innerHTML = sortedParents.map(p => `
                <div class="badge" oncontextmenu="app.showContext(event, 'parent', '${p.key}', null, ${p.expiry})">
                    <div class="badge-info">
                        <span><i class="fa-solid fa-sitemap"></i> ${p.key}</span>
                        <span class="badge-time" data-expiry="${p.expiry}" data-prefix="Exp: ">Exp: ${this.formatTime(p.expiry)}</span>
                    </div>
                </div>
            `).join('');
        }

        const nodesTbody = document.getElementById('nodes-tbody');
        if (entity.nodes.length === 0) {
            nodesTbody.innerHTML = '<tr><td colspan="3" style="text-align:center; color:var(--text-muted); font-style:italic; padding:2rem;">No permissions set</td></tr>';
        } else {
            const sortedNodes = [...entity.nodes].sort((a,b) => this.compareNodes(a.key, b.key));
            nodesTbody.innerHTML = sortedNodes.map(n => {
                const pObj = this.registry.permissions.find(p => p.node === n.key);
                const desc = pObj && pObj.desc ? pObj.desc : '';
                return `
                <tr oncontextmenu="app.showContext(event, 'node', '${n.key}', ${n.value}, ${n.expiry})">
                    <td>
                        <div class="node-cell">
                            <span class="node-key ${desc ? 'has-desc' : ''}" title="${desc}">${n.key}</span>
                        </div>
                    </td>
                    <td>
                        <span class="val-badge ${n.value ? '' : 'false'}">
                            ${n.value ? 'true' : 'false'}
                        </span>
                    </td>
                    <td><span class="time-text" data-expiry="${n.expiry}" data-prefix="">${this.formatTime(n.expiry)}</span></td>
                </tr>
            `}).join('');
        }
    },

    showContext(e, type, key, value, expiry) {
        e.preventDefault();
        this.contextTarget = { type, key, value, expiry };

        const cm = document.getElementById('context-menu');
        const toggleBtn = document.getElementById('cm-toggle');

        if (type === 'node') {
            toggleBtn.style.display = 'flex';
        } else {
            toggleBtn.style.display = 'none';
        }

        cm.style.display = 'block';
        cm.style.left = e.pageX + 'px';
        cm.style.top = e.pageY + 'px';

        const rect = cm.getBoundingClientRect();
        if (rect.bottom > window.innerHeight) cm.style.top = (window.innerHeight - rect.height) + 'px';
        if (rect.right > window.innerWidth) cm.style.left = (window.innerWidth - rect.width) + 'px';
    },

    showSidebarContext(e, type, id, weight) {
        e.preventDefault();
        this.sidebarContextTarget = { type, id, weight };

        const cm = document.getElementById('sidebar-context-menu');
        cm.style.display = 'block';
        cm.style.left = e.pageX + 'px';
        cm.style.top = e.pageY + 'px';

        const rect = cm.getBoundingClientRect();
        if (rect.bottom > window.innerHeight) cm.style.top = (window.innerHeight - rect.height) + 'px';
        if (rect.right > window.innerWidth) cm.style.left = (window.innerWidth - rect.width) + 'px';
    },

    handleSidebarContext(actionType) {
        const tgt = this.sidebarContextTarget;
        if (!tgt) return;

        if (actionType === 'weight') {
            let html = `
                <label>Set Weight</label>
                <input type="number" id="modal-input" class="modal-input" value="${tgt.weight}">
            `;
            this.openModal(`Edit Weight: ${tgt.id}`, html, () => {
                const w = parseInt(document.getElementById('modal-input').value);
                if (!isNaN(w)) {
                    this.action({ action: 'setGroupWeight', id: tgt.id, weight: w });
                }
            });
        } else if (actionType === 'delete') {
            this.deleteGroup(tgt.id);
        }
        document.getElementById('sidebar-context-menu').style.display = 'none';
    },

    handleContextAction(actionType) {
        const tgt = this.contextTarget;
        if (!tgt) return;
        const isGroup = this.current.type === 'group';

        if (actionType === 'toggle') {
            const act = isGroup ? 'setGroupNode' : 'setUserNode';
            const payload = { action: act, node: tgt.key, value: !tgt.value, expiry: tgt.expiry };
            if(isGroup) payload.id = this.current.id; else payload.uuid = this.current.id;
            this.action(payload);
        } else if (actionType === 'expiry') {
            this.promptExpiry(tgt.key, isGroup, tgt.type === 'parent', tgt.value);
        } else if (actionType === 'delete') {
            const act = isGroup
                ? (tgt.type === 'parent' ? 'removeGroupParent' : 'removeGroupNode')
                : (tgt.type === 'parent' ? 'removeUserParent' : 'removeUserNode');
            const payload = { action: act };
            if(isGroup) payload.id = this.current.id; else payload.uuid = this.current.id;
            if(tgt.type === 'parent') payload.parent = tgt.key; else payload.node = tgt.key;
            this.action(payload);
        }
        document.getElementById('context-menu').style.display = 'none';
    },

    updateGroupDatalist() {
        const list = document.getElementById('groups-datalist');
        list.innerHTML = this.registry.groups.sort((a,b)=>a.localeCompare(b)).map(g => `<option value="${g}">`).join('');
    },

    updateSuggestions(query) {
        const suggBox = document.getElementById('node-suggestions');

        const tokens = query.toLowerCase().split(' ').filter(t => t.length > 0);

        const filtered = this.registry.permissions.filter(pObj => {
            const node = pObj.node;
            const isCustom = this.registry.custom.includes(node);
            const lowerNode = node.toLowerCase();

            for (let t of tokens) {
                if (t === '@_custom') { if (!isCustom) return false; }
                else if (t === '@_bukkit') { if (isCustom) return false; }
                else if (t.startsWith('@')) {
                    const targetNs = t.substring(1);
                    const ns = lowerNode.includes('.') ? lowerNode.substring(0, lowerNode.indexOf('.')) : lowerNode;
                    if (!ns.includes(targetNs)) return false;
                }
                else { if (!lowerNode.includes(t)) return false; }
            }
            return true;
        }).sort((a,b) => this.compareNodes(a.node, b.node));

        if (filtered.length === 0) {
            suggBox.style.display = 'none';
            return;
        }

        suggBox.innerHTML = filtered.slice(0, 100).map(pObj => {
            const node = pObj.node;
            const desc = pObj.desc || '';
            return `
            <div class="suggestion-item" onmousedown="document.getElementById('add-node-input').value='${node}'; document.getElementById('node-suggestions').style.display='none';">
                <span style="color:var(--text-main); font-family:monospace; font-size:0.85rem; margin:0;">${node}</span>
                ${desc ? `<span style="color:var(--text-muted); font-size:0.75rem; margin-top:0.2rem;">${desc}</span>` : ''}
            </div>
        `}).join('');
        suggBox.style.display = 'block';
    },

    promptExpiry(key, isGroup, isParent, currentValue = true) {
        let html = `
            <label>Set Duration (e.g., 1w 2d 5h 30m, 0 for perm)</label>
            <input type="text" id="modal-input" class="modal-input" placeholder="Permanent">
        `;
        this.openModal(`Edit Expiry: ${key}`, html, () => {
            const val = document.getElementById('modal-input').value.trim();
            const exp = this.parseDuration(val);
            const act = isGroup ? (isParent ? 'addGroupParent' : 'setGroupNode') : (isParent ? 'addUserParent' : 'setUserNode');
            const payload = { action: act, expiry: exp };
            if(isGroup) payload.id = this.current.id; else payload.uuid = this.current.id;
            if(isParent) payload.parent = key; else { payload.node = key; payload.value = currentValue; }
            this.action(payload);
        });
    },

    createGroup() {
        let html = `
            <label>Group ID (lowercase, numbers, underscores only)</label>
            <input type="text" id="modal-input" class="modal-input" placeholder="e.g. vip">
        `;
        this.openModal('Create New Group', html, () => {
            const id = document.getElementById('modal-input').value.trim().toLowerCase();
            if (id && /^[a-z0-9_]+$/.test(id)) {
                this.action({ action: 'createGroup', id: id });
            } else {
                this.showToast("Invalid group ID.", 'error');
            }
        });
    },

    deleteGroup(id) {
        let html = `<p>Are you sure you want to delete group <strong>${id}</strong>? This cannot be undone.</p>`;
        this.openModal('Delete Group', html, () => {
            this.action({ action: 'deleteGroup', id });
            if (this.current.id === id) {
                this.current = { type: null, id: null };
                document.getElementById('editor-area').style.display = 'none';
                document.getElementById('empty-state').style.display = 'flex';
            }
            this.showToast("Group deleted.");
        });
    },

    submitParent() {
        const input = document.getElementById('add-parent-input');
        const expInput = document.getElementById('add-parent-exp');
        const val = input.value.trim();
        if(!val) return;
        const isGroup = this.current.type === 'group';
        const exp = this.parseDuration(expInput.value.trim());
        const payload = { action: isGroup ? 'addGroupParent' : 'addUserParent', parent: val, expiry: exp };
        if(isGroup) payload.id = this.current.id; else payload.uuid = this.current.id;
        this.action(payload);
        input.value = '';
        expInput.value = '';
    },

    submitNode() {
        const input = document.getElementById('add-node-input');
        const select = document.getElementById('add-node-val');
        const expInput = document.getElementById('add-node-exp');
        const node = input.value.trim();
        if(!node || node.startsWith('@')) return;

        const isGroup = this.current.type === 'group';
        const exp = this.parseDuration(expInput.value.trim());
        const payload = { action: isGroup ? 'setGroupNode' : 'setUserNode', node: node, value: select.value === 'true', expiry: exp };
        if (isGroup) payload.id = this.current.id; else payload.uuid = this.current.id;

        this.action(payload);
        input.value = '';
        expInput.value = '';
        document.getElementById('node-suggestions').style.display = 'none';
    },

    endSession() {
        let html = `<p>Are you sure you want to end this web session? You will need to generate a new link in-game to access the editor again.</p>`;
        this.openModal('End Session', html, () => {
            this.action({ action: 'endSession' });
        });
    },

    openModal(title, bodyHtml, onConfirm) {
        document.getElementById('modal-title').innerText = title;
        document.getElementById('modal-body').innerHTML = bodyHtml;
        const confirmBtn = document.getElementById('modal-confirm');
        confirmBtn.className = title.includes('Delete') || title.includes('End') ? 'btn btn-danger' : 'btn btn-primary';
        confirmBtn.onclick = onConfirm;
        document.getElementById('modal').style.display = 'flex';
        const input = document.getElementById('modal-input');
        if(input) input.focus();
    },

    closeModal() {
        document.getElementById('modal').style.display = 'none';
    }
};

document.getElementById('cm-toggle').addEventListener('click', () => app.handleContextAction('toggle'));
document.getElementById('cm-expiry').addEventListener('click', () => app.handleContextAction('expiry'));
document.getElementById('cm-delete').addEventListener('click', () => app.handleContextAction('delete'));

document.addEventListener('DOMContentLoaded', () => app.init());