package club.sk1er.mods.mousesens.transform.impl;

import club.sk1er.mods.mousesens.transform.FramesTransformer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

import static club.sk1er.mods.mousesens.MouseSensitivityTweak.OBF;

public final class EntityRendererTransformer implements FramesTransformer {

    private List<String> obfPairs = new ArrayList<>();
    private HashMap<String, Integer> varStore = new HashMap<>();

    public EntityRendererTransformer() {
        obfPairs.add("func_181560_a");
        obfPairs.add("func_78480_b");
        varStore.put("1.8", 6);
    }

    @Override
    public String[] getClassNames() {
        return new String[]{"net.minecraft.client.renderer.EntityRenderer"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode method : classNode.methods) {
            boolean flag = false;
            String searge = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(classNode.name, method.name, method.desc);
            for (String obfPair : obfPairs) {
                if (searge.equalsIgnoreCase(obfPair)) {
                    flag = true;
                    break;
                }
            }
            if (method.name.equalsIgnoreCase("updateCameraAndRender") || flag) {
                System.out.println("Isolated Method");
                ListIterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node instanceof FieldInsnNode) {
                        //(((FieldInsnNode) node).name.equalsIgnoreCase("deltaY") || ((FieldInsnNode) node).name.equalsIgnoreCase("field_74375_b") || (((FieldInsnNode) node).name.equalsIgnoreCase("b") && ((FieldInsnNode) node).owner.equalsIgnoreCase(FMLDeobfuscatingRemapper.INSTANCE.unmap("net.minecraft.util.MouseHelper"))))
                        String fieldSearge = FMLDeobfuscatingRemapper.INSTANCE.mapFieldName(((FieldInsnNode) node).owner, ((FieldInsnNode) node).name, ((FieldInsnNode) node).desc);
                        if (fieldSearge.equalsIgnoreCase("field_74375_b") || fieldSearge.equalsIgnoreCase("deltaY")) {
                            AbstractInsnNode next = node.getNext().getNext().getNext().getNext();
                            method.instructions.insert(next, generateFirst());

                            System.out.println("FOUND AND INJECTED");
                            return;
                        }
                    }


                }
            }//TODO obfuscation
        }
    }


    /*
               Store var 'f' (5) with new value from Y sensitivity
               Recalculate 'f1' (6) with 'f' value
               Let it use the new f1 value to mod the Y sensitivity
            */


    private InsnList generateFirst() {
        InsnList insnList = new InsnList();
        insnList.add(new VarInsnNode(Opcodes.ALOAD, 0));
        insnList.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/EntityRenderer", OBF ? "field_78531_r" : "mc", "Lnet/minecraft/client/Minecraft;"));
        insnList.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/Minecraft", OBF ? "field_71417_B" : "mouseHelper", "Lnet/minecraft/util/MouseHelper;"));
        insnList.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/util/MouseHelper", OBF ? "field_74375_b" : "deltaY", "I"));
        insnList.add(new InsnNode(Opcodes.I2F));
        insnList.add(new MethodInsnNode(
                Opcodes.INVOKESTATIC,
                "club/sk1er/mods/mousesens/MouseSensitivityTweak",
                "getSensitivityY", "()F", false
        ));
        insnList.add(new InsnNode(Opcodes.FMUL));
        String version = MinecraftForge.MC_VERSION;


        Integer key = varStore.getOrDefault(version,8);
        if (key == null || key == 0) {
            System.out.println("MouseSensitivityTweak could not identify mappings. Please contact Sk1er for help.");
            return new InsnList();
        } else {
            System.out.println("Detecting mapped version: " + version+" with FSTORE " + 8);
            insnList.add(new VarInsnNode(Opcodes.FSTORE, key));
        }


        return insnList;
    }


}
