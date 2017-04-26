package com.iris.spring.schema;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * Created by chenjinlong on 17/4/26.
 */
public class DLockBeanDefinitionParser implements BeanDefinitionParser {

    private final Class<?> beanClass;

    private final Boolean required;

    public DLockBeanDefinitionParser(Class<?> beanClass, Boolean required) {
        this.beanClass = beanClass;
        this.required = required;
    }


    /**
     * Parse the specified {@link Element} and register the resulting
     * {@link BeanDefinition BeanDefinition(s)} with the
     * {@link ParserContext#getRegistry() BeanDefinitionRegistry}
     * embedded in the supplied {@link ParserContext}.
     * <p>Implementations must return the primary {@link BeanDefinition} that results
     * from the parse if they will ever be used in a nested fashion (for example as
     * an inner tag in a {@code <property/>} tag). Implementations may return
     * {@code null} if they will <strong>not</strong> be used in a nested fashion.
     *
     * @param element       the element that is to be parsed into one or more {@link BeanDefinition BeanDefinitions}
     * @param parserContext the object encapsulating the current state of the parsing process;
     *                      provides access to a {@link BeanDefinitionRegistry}
     * @return the primary {@link BeanDefinition}
     */
    //TODO ʵ���ҡ�����
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        return null;
    }
}