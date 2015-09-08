package com.noubase.core.crud.repository;

import com.google.common.collect.Sets;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.support.RepositoryProxyPostProcessor;

import java.util.Set;

/**
 * Created by rshuper on 17.08.15.
 */
class ProxyPostProcessor implements RepositoryProxyPostProcessor {

    @Override
    public void postProcess(ProxyFactory factory, RepositoryInformation repositoryInformation) {
        Set<Class<?>> interfaces = Sets.newHashSet(repositoryInformation.getRepositoryInterface().getInterfaces());
        if (interfaces.contains(ResourceRepository.class)) {
            factory.addInterface(PatchableRepository.class);
        }
    }
}
