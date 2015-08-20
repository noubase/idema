package com.noubase.core.crud.repository;

import org.springframework.aop.framework.ProxyFactory;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.support.RepositoryProxyPostProcessor;

/**
 * Created by rshuper on 17.08.15.
 */
class ProxyPostProcessor implements RepositoryProxyPostProcessor {

    @Override
    public void postProcess(ProxyFactory factory, RepositoryInformation repositoryInformation) {
        factory.addInterface(PatchableRepository.class);
    }
}
