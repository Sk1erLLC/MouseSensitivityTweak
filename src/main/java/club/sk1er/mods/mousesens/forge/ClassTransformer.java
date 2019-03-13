package club.sk1er.mods.mousesens.forge;

import club.sk1er.mods.mousesens.transform.FramesTransformer;
import club.sk1er.mods.mousesens.transform.impl.EntityRendererTransformer;
import club.sk1er.mods.mousesens.transform.impl.GuiOptionsTransformer;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.launchwrapper.IClassTransformer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.util.*;

public final class ClassTransformer implements IClassTransformer {

    private static final Logger LOGGER = LogManager.getLogger("ASM");

    private final Multimap<String, FramesTransformer> transformerMap = ArrayListMultimap.create();

    public ClassTransformer() {
        this.registerTransformer(new EntityRendererTransformer());
        this.registerTransformer(new GuiOptionsTransformer());
    }

    private void registerTransformer(FramesTransformer transformer) {
        for (String cls : transformer.getClassNames()) {
            this.transformerMap.put(cls, transformer);
        }
    }


    @Override public byte[] transform(String name, String transformedName, byte[] bytes) {
        if (bytes == null) return null;


        Collection<FramesTransformer> transformers = this.transformerMap.get(transformedName);
        if (transformers.isEmpty()) return bytes;

        LOGGER.info("Found {} transformers for {}", transformers.size(), transformedName);

        ClassReader classReader = new ClassReader(bytes);
        ClassNode classNode = new ClassNode();
        classReader.accept(classNode, ClassReader.EXPAND_FRAMES);

        transformers.forEach(transformer -> {
            LOGGER.info("Applying transformer {} on {}...",
                    transformer.getClass().getName(), transformedName);
            transformer.transform(classNode, transformedName);
        });

        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        try {
            classNode.accept(classWriter);
        } catch (Throwable e) {
            System.out.println("Exception when transforming " + transformedName + " : " + e.getClass().getSimpleName());
            e.printStackTrace();
        }

        return classWriter.toByteArray();
    }

}
