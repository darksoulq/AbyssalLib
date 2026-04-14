package com.github.darksoulq.abyssallib.common.model.blockbench;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.darksoulq.abyssallib.common.model.blockbench.animation.BBAnimation;
import com.github.darksoulq.abyssallib.common.model.blockbench.animation.BBAnimator;
import com.github.darksoulq.abyssallib.common.model.blockbench.animation.BBEasing;
import com.github.darksoulq.abyssallib.common.model.blockbench.animation.BBKeyframe;
import com.github.darksoulq.abyssallib.common.model.blockbench.meta.BBMeta;
import com.github.darksoulq.abyssallib.common.model.blockbench.meta.BBResolution;
import com.github.darksoulq.abyssallib.common.model.blockbench.texture.BBTexture;
import com.github.darksoulq.abyssallib.common.model.blockbench.tree.BBElement;
import com.github.darksoulq.abyssallib.common.model.blockbench.tree.BBGroup;
import com.github.darksoulq.abyssallib.common.serialization.ops.JsonOps;
import org.bukkit.plugin.Plugin;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class BBModelLoader {

    private BBModelLoader() {}

    public static BBModel load(Plugin plugin, String namespace, String path) {
        String resourcePath = "resourcepack/" + namespace + "/models/" + path + ".bbmodel";
        try (InputStream in = plugin.getResource(resourcePath)) {
            if (in == null) throw new RuntimeException(resourcePath);
            return load(JsonOps.INSTANCE.mapper.readTree(in));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static BBModel load(byte[] data) {
        try {
            return load(JsonOps.INSTANCE.mapper.readTree(data));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static BBModel load(JsonNode root) {
        BBModel model = new BBModel();

        if (root.has("meta")) {
            JsonNode mNode = root.get("meta");
            model.setMeta(new BBMeta(
                optStr(mNode, "format_version"),
                optStr(mNode, "model_format"),
                optBool(mNode, "box_uv", false)
            ));
        }

        model.setName(optStr(root, "name"));
        model.setModelIdentifier(optStr(root, "model_identifier"));
        model.setVisibleBox(readVec3(root.get("visible_box")));

        if (root.has("resolution")) {
            JsonNode rNode = root.get("resolution");
            model.setResolution(new BBResolution(optInt(rNode, "width", 64), optInt(rNode, "height", 64)));
        }

        List<String> texMap = new ArrayList<>();

        if (root.has("textures")) {
            for (JsonNode tNode : root.get("textures")) {
                int[] frames = new int[0];
                if (tNode.has("frames")) {
                    JsonNode fNode = tNode.get("frames");
                    frames = new int[fNode.size()];
                    for (int i = 0; i < fNode.size(); i++) frames[i] = fNode.get(i).asInt();
                }
                BBTexture texture = new BBTexture(
                    optStr(tNode, "uuid"), optStr(tNode, "id"), optStr(tNode, "name"), optStr(tNode, "path"),
                    optStr(tNode, "folder"), optStr(tNode, "namespace"), optStr(tNode, "group"), optBool(tNode, "particle", false),
                    optStr(tNode, "render_mode"), optStr(tNode, "render_sides"), optInt(tNode, "fps", 7),
                    optStr(tNode, "frame_order_type"), optBool(tNode, "visible", true), optStr(tNode, "source"),
                    optInt(tNode, "uv_width", 16), optInt(tNode, "uv_height", 16), optInt(tNode, "frame_time", 1),
                    optBool(tNode, "frame_interpolate", false), frames
                );
                model.getTextures().put(texture.getUuid(), texture);
                texMap.add(texture.getUuid());
            }
        }

        if (root.has("elements")) {
            for (JsonNode eNode : root.get("elements")) {
                BBElement element = new BBElement(
                    optStr(eNode, "uuid"), optStr(eNode, "name"), optStr(eNode, "type"), optBool(eNode, "box_uv", false),
                    optStr(eNode, "render_order"), optBool(eNode, "locked", false), optBool(eNode, "export", true),
                    optInt(eNode, "color", 0), optInt(eNode, "autouv", 0), readVec3(eNode.get("from")),
                    readVec3(eNode.get("to")), readVec3(eNode.get("origin")), readVec3(eNode.get("rotation"))
                );
                if (eNode.has("faces")) {
                    eNode.get("faces").fields().forEachRemaining(entry -> {
                        JsonNode fNode = entry.getValue();
                        String texRef = null;
                        JsonNode texNode = fNode.get("texture");
                        if (texNode != null && !texNode.isNull()) {
                            texRef = texNode.isNumber() && texNode.asInt() < texMap.size() ? texMap.get(texNode.asInt()) : texNode.asText();
                        }
                        element.getFaces().put(entry.getKey(), new BBElement.BBFace(readVec4(fNode.get("uv")), texRef));
                    });
                }
                model.getElements().put(element.getUuid(), element);
            }
        }

        Map<String, BBGroup> parsedGroups = new HashMap<>();
        if (root.has("groups")) {
            for (JsonNode gNode : root.get("groups")) {
                BBGroup group = new BBGroup(
                    optStr(gNode, "uuid"), optStr(gNode, "name"), optBool(gNode, "export", true), optBool(gNode, "locked", false),
                    optInt(gNode, "color", 0), optBool(gNode, "shade", true), optBool(gNode, "mirror_uv", false),
                    optBool(gNode, "visibility", true), optInt(gNode, "autouv", 0), readVec3(gNode.get("origin")), readVec3(gNode.get("rotation"))
                );
                parsedGroups.put(group.getUuid(), group);
                model.getGroupCache().put(group.getUuid(), group);
            }
        }

        if (root.has("outliner")) {
            for (JsonNode node : root.get("outliner")) {
                buildOutlinerTree(node, model, parsedGroups, null);
            }
        }

        if (root.has("animations")) {
            for (JsonNode aNode : root.get("animations")) {
                BBAnimation animation = new BBAnimation(
                    optStr(aNode, "uuid"), optStr(aNode, "name"), optStr(aNode, "loop"), optBool(aNode, "override", false),
                    (float) optDouble(aNode, "length", 0), optInt(aNode, "snapping", 24), optStr(aNode, "blend_weight"),
                    optStr(aNode, "start_delay"), optStr(aNode, "loop_delay")
                );
                if (aNode.has("animators")) {
                    aNode.get("animators").fields().forEachRemaining(entry -> {
                        JsonNode anNode = entry.getValue();
                        BBAnimator animator = new BBAnimator(optStr(anNode, "name"), optStr(anNode, "uuid"), optStr(anNode, "type"), optBool(anNode, "rotation_global", false), optBool(anNode, "quaternion_interpolation", false));
                        if (anNode.has("keyframes")) {
                            for (JsonNode kfNode : anNode.get("keyframes")) {
                                Vector3f dp = new Vector3f();
                                if (kfNode.has("data_points") && kfNode.get("data_points").size() > 0) {
                                    JsonNode dpNode = kfNode.get("data_points").get(0);
                                    dp.set((float) optDouble(dpNode, "x", 0), (float) optDouble(dpNode, "y", 0), (float) optDouble(dpNode, "z", 0));
                                }
                                animator.addKeyframe(new BBKeyframe(optStr(kfNode, "uuid"), optStr(kfNode, "channel"), (float) optDouble(kfNode, "time", 0), optInt(kfNode, "color", -1), BBEasing.parse(optStr(kfNode, "interpolation")), optBool(kfNode, "uniform", false), dp, readVec3(kfNode.get("bezier_left_time")), readVec3(kfNode.get("bezier_left_value")), readVec3(kfNode.get("bezier_right_time")), readVec3(kfNode.get("bezier_right_value"))));
                            }
                        }
                        animator.sort();
                        animation.getAnimators().put(animator.getName(), animator);
                    });
                }
                model.getAnimations().put(animation.getName(), animation);
            }
        }
        return model;
    }

    private static void buildOutlinerTree(JsonNode node, BBModel model, Map<String, BBGroup> parsedGroups, BBGroup parent) {
        if (node.isTextual()) {
            if (parent != null) parent.getChildElements().add(node.asText());
            return;
        }
        if (node.isObject()) {
            String uuid = optStr(node, "uuid");
            BBGroup group = parsedGroups.get(uuid);
            if (group != null) {
                if (parent == null) model.getRootGroups().add(group);
                else parent.getChildren().add(group);
                if (node.has("children")) {
                    for (JsonNode child : node.get("children")) buildOutlinerTree(child, model, parsedGroups, group);
                }
            } else if (node.has("children")) {
                BBGroup fallbackGroup = new BBGroup(uuid, optStr(node, "name"), optBool(node, "export", true), optBool(node, "locked", false), optInt(node, "color", 0), optBool(node, "shade", true), optBool(node, "mirror_uv", false), optBool(node, "visibility", true), optInt(node, "autouv", 0), readVec3(node.get("origin")), readVec3(node.get("rotation")));
                model.getGroupCache().put(fallbackGroup.getUuid(), fallbackGroup);
                if (parent == null) model.getRootGroups().add(fallbackGroup);
                else parent.getChildren().add(fallbackGroup);
                for (JsonNode child : node.get("children")) buildOutlinerTree(child, model, parsedGroups, fallbackGroup);
            }
        }
    }

    private static Vector3f readVec3(JsonNode node) {
        if (node == null || !node.isArray() || node.size() < 3) return new Vector3f();
        return new Vector3f(node.get(0).floatValue(), node.get(1).floatValue(), node.get(2).floatValue());
    }

    private static Vector4f readVec4(JsonNode node) {
        if (node == null || !node.isArray() || node.size() < 4) return new Vector4f();
        return new Vector4f(node.get(0).floatValue(), node.get(1).floatValue(), node.get(2).floatValue(), node.get(3).floatValue());
    }

    private static String optStr(JsonNode node, String key) {
        return node.has(key) ? node.get(key).asText() : "";
    }

    private static int optInt(JsonNode node, String key, int def) {
        return node.has(key) ? node.get(key).asInt() : def;
    }

    private static boolean optBool(JsonNode node, String key, boolean def) {
        return node.has(key) ? node.get(key).asBoolean() : def;
    }

    private static double optDouble(JsonNode node, String key, double def) {
        if (!node.has(key)) return def;
        JsonNode val = node.get(key);
        if (val.isNumber()) return val.asDouble();
        if (val.isTextual()) {
            try { return Double.parseDouble(val.asText()); } catch (NumberFormatException ignored) {}
        }
        return def;
    }
}