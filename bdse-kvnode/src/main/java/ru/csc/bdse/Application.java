package ru.csc.bdse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import ru.csc.bdse.kv.BerkleyKeyValueApi;
import ru.csc.bdse.kv.KeyValueApi;
import ru.csc.bdse.kv.NodeRepository;
import ru.csc.bdse.util.Env;
import ru.csc.bdse.util.NodeOperationException;

import java.util.UUID;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    private static String randomNodeName() {
        return "kvnode-" + UUID.randomUUID().toString().substring(4);
    }

    @Bean
    @Primary
    @Autowired
    KeyValueApi node(BerkleyKeyValueApi berkleyKeyValueApi, NodeRepository nodeRepository) {
        String nodeName = Env.get(Env.KVNODE_NAME).orElseGet(Application::randomNodeName);
        try{
            nodeRepository.addNode(nodeName);
        } catch (NodeOperationException e) {
            throw new RuntimeException(e);
        }
        return berkleyKeyValueApi;
    }
}
