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

import static club.sk1er.mods.mousesens.MouseSensitivityTweak.OBF;

public final class GuiOptionsTransformer implements FramesTransformer {

	@Override
	public String[] getClassNames() {
		return new String[]{"net.minecraft.client.gui.GuiControls"};
	}

	@Override
	public void transform(ClassNode classNode, String name) {
		for (MethodNode method : classNode.methods) {
            if (method.name.equalsIgnoreCase("initGui") || method.name.equalsIgnoreCase("func_73866_w_") || method.name.equalsIgnoreCase("b") && method.desc.equalsIgnoreCase("()V")) {
			    System.out.println("Hooked GuiControls#initGui via" + method.name);
				method.instructions.insertBefore(method.instructions.getLast().getPrevious(),generateFirst());

            }
		}
	}


    /*
             call MouseSensitvityTweak#applyGuiChanges(buttonList,width,height);

            */
	private InsnList generateFirst() {
		InsnList insnList = new InsnList();
		insnList.add(new VarInsnNode(Opcodes.ALOAD,0));
        insnList.add(new FieldInsnNode(Opcodes.GETFIELD,"net/minecraft/client/gui/GuiControls",OBF ? "field_146292_n": "buttonList","Ljava/util/List;"));
        insnList.add(new VarInsnNode(Opcodes.ALOAD,0));
        insnList.add(new FieldInsnNode(Opcodes.GETFIELD,"net/minecraft/client/gui/GuiControls",OBF ? "field_146294_l": "width","I"));
        insnList.add(new VarInsnNode(Opcodes.ALOAD,0));
        insnList.add(new FieldInsnNode(Opcodes.GETFIELD,"net/minecraft/client/gui/GuiControls",OBF ? "field_146295_m": "height","I"));
		insnList.add(new MethodInsnNode(
				Opcodes.INVOKESTATIC,
				"club/sk1er/mods/mousesens/MouseSensitivityTweak",
				"applyGuiChanges", "(Ljava/util/List;II)V", false
		));
		return insnList;
	}





}
