package com.qianyitian.hope2.analyzer.engine;

import com.googlecode.aviator.runtime.function.AbstractFunction;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;
import org.springframework.util.SystemPropertyUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FunctionUtil {

    public static List<AbstractFunction> findAllFunctions() {
        try {
            List<Class<AbstractFunction>> classList = findMyTypes("com.qianyitian.hope2.analyzer.engine.function");
            List<AbstractFunction> functionsList = classList.parallelStream().map(clazz -> {
                try {
                    return clazz.getDeclaredConstructor().newInstance();
                } catch (ReflectiveOperationException e) {
                    throw new RuntimeException(e);
                }
            }).collect(Collectors.toList());
            return functionsList;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    private static List<Class<AbstractFunction>> findMyTypes(String basePackage) throws IOException, ClassNotFoundException {
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resourcePatternResolver);

        List<Class<AbstractFunction>> candidates = new ArrayList<Class<AbstractFunction>>();
        String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                resolveBasePackage(basePackage) + "/" + "**/*.class";
        Resource[] resources = resourcePatternResolver.getResources(packageSearchPath);
        for (Resource resource : resources) {
            if (resource.isReadable()) {
                MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                if (isCandidate(metadataReader)) {
                    Class<AbstractFunction> aClass = (Class<AbstractFunction>) Class.forName(metadataReader.getClassMetadata().getClassName());
                    candidates.add(aClass);
                }
            }
        }
        return candidates;
    }

    private static String resolveBasePackage(String basePackage) {
        return ClassUtils.convertClassNameToResourcePath(SystemPropertyUtils.resolvePlaceholders(basePackage));
    }

    private static boolean isCandidate(MetadataReader metadataReader) throws ClassNotFoundException {
        Class c = Class.forName(metadataReader.getClassMetadata().getClassName());
        return AbstractFunction.class.isAssignableFrom(c);
    }
}
