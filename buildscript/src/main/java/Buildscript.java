import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Collectors;
import io.github.coolcrabs.brachyura.decompiler.BrachyuraDecompiler;
import io.github.coolcrabs.brachyura.decompiler.fernflower.FernflowerDecompiler;
import io.github.coolcrabs.brachyura.dependency.JavaJarDependency;
import io.github.coolcrabs.brachyura.fabric.FabricContext;
import io.github.coolcrabs.brachyura.fabric.FabricContext.ModDependencyCollector;
import io.github.coolcrabs.brachyura.fabric.FabricLoader;
import io.github.coolcrabs.brachyura.fabric.FabricMaven;
import io.github.coolcrabs.brachyura.fabric.Intermediary;
import io.github.coolcrabs.brachyura.fabric.SimpleFabricProject;
import io.github.coolcrabs.brachyura.fabric.SimpleFabricProject.SimpleFabricContext;
import io.github.coolcrabs.brachyura.fabric.Yarn;
import io.github.coolcrabs.brachyura.maven.Maven;
import io.github.coolcrabs.brachyura.maven.MavenId;
import io.github.coolcrabs.brachyura.minecraft.Minecraft;
import io.github.coolcrabs.brachyura.minecraft.VersionMeta;
import io.github.coolcrabs.brachyura.processing.sinks.AtomicZipProcessingSink;
import io.github.coolcrabs.brachyura.processing.sources.DirectoryProcessingSource;
import io.github.coolcrabs.brachyura.util.Util;
import io.github.coolcrabs.brachyura.processing.ProcessorChain;
import net.fabricmc.mappingio.tree.MappingTree;

public class Buildscript extends SimpleFabricProject {
    @Override
    protected FabricContext createContext() {
        return new IntermediaryPatcher();
    }
    @Override
    public VersionMeta createMcVersion() {
        // Minecraft Version
        return Minecraft.getVersion("1.8.9");
    }

    @Override
    public MappingTree createMappings() {
        // Uses Mojang Official Mappings
        return Yarn.ofMaven("https://maven.legacyfabric.net", FabricMaven.yarn("1.8.9+build.202203281833")).tree;
    }

    @Override
    public FabricLoader getLoader() {
        // Fabric Loader Version
        return new FabricLoader(FabricMaven.URL, FabricMaven.loader("0.13.3"));
    }

    @Override
    public String getModId() {
        // Mod Name
        return "togglesprint";
    }

    @Override
    public String getVersion() {
        // Mod Version
        return "0.1.0";
    }

    @Override
    public void getModDependencies(ModDependencyCollector d) {}

    @Override
    public BrachyuraDecompiler decompiler() {
        // Uses QuiltFlower instead of CFR
        return new FernflowerDecompiler(Maven.getMavenJarDep("https://maven.quiltmc.org/repository/release", new MavenId("org.quiltmc:quiltflower:1.7.0"))); 
    };

    @Override
    public Path getBuildJarPath() {
        // Changes the jar file name
        return getBuildLibsDir().resolve(getModId() + "-" + "mc" + createMcVersion().version + "-" + getVersion() + ".jar");
    }

    @Override
    public JavaJarDependency build() {
        // Fixes fabric.mod.json versioning
        try {
            try (AtomicZipProcessingSink out = new AtomicZipProcessingSink(getBuildJarPath())) {
                context.get().modDependencies.get(); // Ugly hack
                new ProcessorChain(context.get().resourcesProcessingChain(jijList), new FmjVersionFixer(this)).apply(out, Arrays.stream(getResourceDirs()).map(DirectoryProcessingSource::new).collect(Collectors.toList()));
                context.get().getRemappedClasses(module.get()).values().forEach(s -> s.getInputs(out));
                out.commit();
            }
            return new JavaJarDependency(getBuildJarPath(), null, getId());
        } catch (Exception e) {
            throw Util.sneak(e);
        }
    }

    public class IntermediaryPatcher extends SimpleFabricContext {
        @Override
        protected MappingTree createIntermediary() {
            return Intermediary.ofMaven("https://maven.legacyfabric.net", new MavenId("net.fabricmc", "intermediary", "1.8.9")).tree;
        }
    }
}
