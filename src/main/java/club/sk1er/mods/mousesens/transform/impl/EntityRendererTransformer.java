package club.sk1er.mods.mousesens.transform.impl;

import club.sk1er.mods.mousesens.transform.FramesTransformer;
import net.minecraft.client.Minecraft;
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
import scala.reflect.NameTransformer;

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
						AbstractInsnNode next = node.getNext().getNext().getNext().getNext();
						System.out.println(next);

                        method.instructions.insert(next, generateFirst());

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

//		insnList.add(new MethodInsnNode(
//				Opcodes.INVOKESTATIC,
//				"club/sk1er/mods/mousesens/MouseSensitivityTweak",
//				"getSensitivityX", "()F", false
//		));
//		insnList.add(new VarInsnNode(Opcodes.FSTORE, 7));


		insnList.add(new VarInsnNode(Opcodes.ALOAD,0));
		insnList.add(new FieldInsnNode(Opcodes.GETFIELD,"net/minecraft/client/renderer/EntityRenderer","mc","Lnet/minecraft/client/Minecraft;"));
		insnList.add(new FieldInsnNode(Opcodes.GETFIELD,"net/minecraft/client/Minecraft","mouseHelper","Lnet/minecraft/util/MouseHelper;"));
		insnList.add(new FieldInsnNode(Opcodes.GETFIELD,"net/minecraft/util/MouseHelper","deltaY","I"));
		insnList.add(new InsnNode(Opcodes.I2F));
		insnList.add(new MethodInsnNode(
				Opcodes.INVOKESTATIC,
				"club/sk1er/mods/mousesens/MouseSensitivityTweak",
				"getSensitivityY", "()F", false
		));
		insnList.add(new InsnNode(Opcodes.FMUL));
		insnList.add(new VarInsnNode(Opcodes.FSTORE, 8));

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
