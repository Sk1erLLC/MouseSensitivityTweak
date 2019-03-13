package club.sk1er.mods.mousesens.transform.impl;

import club.sk1er.mods.mousesens.transform.FramesTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.ListIterator;

public final class EntityRendererTransformer implements FramesTransformer {

    @Override
    public String[] getClassNames() {
        return new String[]{"net.minecraft.client.renderer.EntityRenderer"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode method : classNode.methods) {
            if (method.name.equalsIgnoreCase("updateCameraAndRender")) {
                ListIterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node instanceof FieldInsnNode && ((FieldInsnNode) node).name.equalsIgnoreCase("deltaY")) {
                        AbstractInsnNode previous = node.getPrevious().getPrevious().getPrevious().getPrevious();
                        System.out.println(previous);
                        if(previous instanceof VarInsnNode) {
                            System.out.println(((VarInsnNode) previous).var);
                            System.out.println(previous.getOpcode());
                        }

                        if(previous instanceof FieldInsnNode) {
                            System.out.println(previous.getOpcode() + " " + ((FieldInsnNode) previous).owner + " " + ((FieldInsnNode) previous).name);
                        }
                        method.instructions.insert(previous, generateFirst());
                        method.instructions.insert(previous, generateSecond());

                        System.out.println("FOUND AND INJECTED");
                        return;
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

        insnList.add(new MethodInsnNode(
                Opcodes.INVOKESTATIC,
                "club/sk1er/mods/mousesens/MouseSensitivityTweak",
                "getMouseSensitivity", "()F", false
        ));

        insnList.add(new LdcInsnNode(0.6));
        insnList.add(new InsnNode(Opcodes.FMUL));
        insnList.add(new LdcInsnNode(0.2));
        insnList.add(new InsnNode(Opcodes.FADD));
        insnList.add(new VarInsnNode(Opcodes.FSTORE, 5));
        return insnList;
    }


    private InsnList generateSecond() {

        InsnList insnList = new InsnList();
        insnList.add(new VarInsnNode(Opcodes.FLOAD, 5));
        insnList.add(new VarInsnNode(Opcodes.FLOAD, 5));
        insnList.add(new InsnNode(Opcodes.FMUL));
        insnList.add(new VarInsnNode(Opcodes.FLOAD, 5));
        insnList.add(new InsnNode(Opcodes.FMUL));
        insnList.add(new LdcInsnNode(8.0));
        insnList.add(new InsnNode(Opcodes.FMUL));
        insnList.add(new VarInsnNode(Opcodes.FSTORE, 6));


        return insnList;
    }



}
