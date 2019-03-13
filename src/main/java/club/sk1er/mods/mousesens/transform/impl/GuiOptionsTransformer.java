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

public final class GuiOptionsTransformer implements FramesTransformer {

	@Override
	public String[] getClassNames() {
		return new String[]{"net.minecraft.client.gui.GuiOptions"};
	}

	@Override
	public void transform(ClassNode classNode, String name) {
		for (MethodNode method : classNode.methods) {
			if (method.name.equalsIgnoreCase("initGui")) {
			    System.out.println("Hooked GuiOptions#initGui");
				method.instructions.insertBefore(method.instructions.getLast(),generateFirst());

            }
		}
	}


    /*
             call MouseSensitvityTweak#applyGuiChanges(buttonList,width,height);

            */
	private InsnList generateFirst() {
		InsnList insnList = new InsnList();



		insnList.add(new VarInsnNode(Opcodes.ALOAD,0));
        insnList.add(new FieldInsnNode(Opcodes.GETFIELD,"net/minecraft/client/gui/GuiOptions","buttonList","Ljava/util/List;"));
        insnList.add(new FieldInsnNode(Opcodes.GETFIELD,"net/minecraft/client/gui/GuiOptions","width","I"));
        insnList.add(new FieldInsnNode(Opcodes.GETFIELD,"net/minecraft/client/gui/GuiOptions","height","I"));
		insnList.add(new MethodInsnNode(
				Opcodes.INVOKESTATIC,
				"club/sk1er/mods/mousesens/MouseSensitivityTweak",
				"applyGuiChanges", "(Ljava/util/List;,I,I)V", false
		));
		return insnList;
	}





}
