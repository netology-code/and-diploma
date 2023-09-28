package ru.netology.nework.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.PathItem
import io.swagger.v3.oas.models.parameters.HeaderParameter
import org.springdoc.core.customizers.OpenApiCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class OpenapiConfiguration {

    @Bean
    fun consumerTypeHeaderOpenAPICustomizer(): OpenApiCustomizer =
        OpenApiCustomizer { openApi: OpenAPI ->
            openApi.paths.values.stream().flatMap { pathItem: PathItem ->
                pathItem.readOperations().stream()
            }
                .forEach { operation: Operation ->
                    operation.addParametersItem(
                        HeaderParameter()
                            .name("Api-Key")
                            .required(true)
                            .description("Ключ для доступа к серверу получите в ЛК")
                    )
                }
        }
}
