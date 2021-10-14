package es.um.asio.service.proxy.impl;

import es.um.asio.service.model.relational.Attribute;
import es.um.asio.service.model.relational.DiscoveryApplication;
import es.um.asio.service.model.relational.JobRegistry;
import es.um.asio.service.model.relational.Value;
import es.um.asio.service.proxy.AttributeProxy;
import es.um.asio.service.proxy.DiscoveryApplicationProxy;
import es.um.asio.service.proxy.ValueProxy;
import es.um.asio.service.repository.relational.DiscoveryApplicationRepository;
import es.um.asio.service.repository.relational.ValueRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Proxy for JobRegistryProxyImp repository implementation.
 * @implNote JobRegistryProxyImp
 * @see JobRegistry
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
@Service
public class DiscoveryApplicationProxyImp implements DiscoveryApplicationProxy {

    private final Logger logger = LoggerFactory.getLogger(DiscoveryApplicationProxyImp.class);

    @Autowired
    DiscoveryApplicationRepository discoveryApplicationRepository;


    @Override
    public DiscoveryApplication save(DiscoveryApplication da) {
        if (findById(da.getId()).isEmpty()) {
            discoveryApplicationRepository.insertNoNested(da.getId(),da.getName(),da.getPid(),da.getStartDate(),da.getVersion());
        }
        return da;
    }

    @Override
    public Optional<DiscoveryApplication> findById(String id) {
        Optional<DiscoveryApplication> requestRegistries = discoveryApplicationRepository.findById(id);
        if (!requestRegistries.isEmpty()) {
            return Optional.of(requestRegistries.get());
        } else {
            return Optional.empty();
        }
    }

}
