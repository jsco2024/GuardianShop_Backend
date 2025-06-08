package com.ms_security.ms_security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

public class CachingConfig {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new Hibernate5Module()); // Registro de módulo para manejar las referencias LAZY
        objectMapper.findAndRegisterModules();

        RedisSerializer<Object> jsonSerializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        RedisCacheConfiguration cacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jsonSerializer))
                .entryTtl(Duration.ofMinutes(15)) // Configura el tiempo de expiración de las entradas en la caché
                .disableCachingNullValues(); // Deshabilita el rechazo de valores nulos en la caché

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(cacheConfiguration)
                .build();
    }

    /*
    CONFIGURACION LOCAL
*/
    @Bean
    public CacheManager cacheManager() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager() {
            @Override
            protected Cache createConcurrentMapCache(String name) {
                return new ConcurrentMapCache(name, true);
            }
        };

        List<String> cacheNames = Arrays.asList(
                "ContactFormFindById",
                "ContactFormFindAll",
                "ContactFormFindByEmail",
                "PermissionFindById",
                "PermissionFindAll",
                "PermissionFindByName",
                "PermissionFindByRoleId",
                "RoleFindById",
                "RoleFindAll",
                "RoleFindByName",
                "RoleFindRoleWithPermissionById",
                "ServicesFindById",
                "ServicesFindByAll",
                "ServicesFindByCode",
                "ServicesFindByName",
                "ServiceFindByCategoryId",
                "ServiceFindByCodeAndCategoryId",
                "UserFindById",
                "UserFindByAll",
                "UserFindByUserName",
                "UserFindUserWithRolesById",
                "UserFindByEmail",
                "InventoryFindById",
                "InventoryFindAll",
                "InventoryFindByCode",
                "InventoryFindAllByIds",
                "InventoryFindAllByServiceId",
                "CategoryFindById",
                "CategoryFindAll",
                "CategoryFindByName",
                "CartFindById",
                "CartFindAll",
                "CartFindByUserName",
                "OrderFindById",
                "OrderFindAll",
                "OrderMaxNumber",
                "OrderFindByOrderNumber",
                "OrderFindAllByStatus",
                "OrderFindByOrderNumberWithItems",
                "OrderItemFindById",
                "OrderItemFindAll",
                "OrderItemFindByProductAndCart",
                "OrderItemFindByOrderId",
                "OrderItemFindByCartAndUser",
                "OrderItemFindByCartIdPage",
                "OrderItemFindByCartId",
                "EntriesFindById",
                "EntriesFindAll",
                "EntriesFindByInvoiceNumber",
                "EntriesConsecutive",
                "ExitsFindById",
                "ExitsFindAll",
                "ExitsConsecutive",
                "ParametersFindById",
                "ParametersFindAll",
                "ParametersFindByCode"
        );

        cacheManager.setCacheNames(cacheNames);

        return cacheManager;
    }
}
