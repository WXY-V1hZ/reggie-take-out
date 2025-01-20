package com.nenood.reggie.config;

import com.nenood.reggie.common.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@Slf4j
@MapperScan("com.nenood.reggie.mapper")
public class WebMvcConfig implements WebMvcConfigurer {
    /**
     * 静态资源映射
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/");
        registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/");
        log.info("Static resource mapping...");
    }

    /**
     * 由于JavaScript的Long类型只能保证16位的精度，直接传整型数据会导致精度丢失从而造成ID错误
     * 添加自定义的消息转换器，在传值之前将整型数据转换成字符串
     */
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        log.info("已设置消息转换器");
        // 创建消息转换器对象
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        // 设置对象转换器，底层使用Jackson将Java对象转为json
        converter.setObjectMapper(new JacksonObjectMapper());
        // 将创建好的消息转换器追加到mvc框架的转换器集合中，将index设置为0，优先使用自己的
        converters.add(0, converter);
    }
}
