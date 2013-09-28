/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2012, The THYMELEAF team (http://www.thymeleaf.org)
 * 
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 * 
 * =============================================================================
 */
package org.thymeleaf.standard.expression;

import org.thymeleaf.Configuration;
import org.thymeleaf.exceptions.TemplateProcessingException;


/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.1.0
 *
 */
public final class StandardExpressions {


    public static final String STANDARD_EXPRESSION_EXECUTOR_ATTRIBUTE_NAME = "StandardExpressionExecutor";
    public static final String STANDARD_EXPRESSION_PARSER_ATTRIBUTE_NAME = "StandardExpressionParser";
    public static final String STANDARD_CONVERSION_SERVICE_ATTRIBUTE_NAME = "StandardConversionService";



    private StandardExpressions() {
        super();
    }


    
    public static StandardExpressionParser getExpressionParser(final Configuration configuration) {
        final Object parser =
                configuration.getExecutionAttributes().get(STANDARD_EXPRESSION_PARSER_ATTRIBUTE_NAME);
        if (parser == null || (!(parser instanceof StandardExpressionParser))) {
            throw new TemplateProcessingException(
                    "No Standard Expression Parser has been registered as an execution argument. " +
                    "This is a requirement for using " + StandardExpressions.class.getSimpleName() + ", and might happen " +
                    "if neither the Standard or the SpringStandard dialects have " +
                    "been added to the Template Engine and none of the specified dialects registers an " +
                    "attribute of type " + StandardExpressionParser.class.getName() + " with name " +
                    "\"" + STANDARD_EXPRESSION_PARSER_ATTRIBUTE_NAME + "\"");
        }
        return (StandardExpressionParser) parser;
    }




    public static StandardExpressionExecutor getExpressionExecutor(final Configuration configuration) {
        final Object executor =
                configuration.getExecutionAttributes().get(STANDARD_EXPRESSION_EXECUTOR_ATTRIBUTE_NAME);
        if (executor == null || (!(executor instanceof StandardExpressionExecutor))) {
            throw new TemplateProcessingException(
                    "No Standard Expression Executor has been registered as an execution argument. " +
                    "This is a requirement for using " + StandardExpressions.class.getSimpleName() + ", and might happen " +
                    "if neither the Standard or the SpringStandard dialects have " +
                    "been added to the Template Engine and none of the specified dialects registers an " +
                    "attribute of type " + StandardExpressionExecutor.class.getName() + " with name " +
                    "\"" + STANDARD_EXPRESSION_EXECUTOR_ATTRIBUTE_NAME + "\"");
        }
        return (StandardExpressionExecutor) executor;
    }





    public static IStandardConversionService getConversionService(final Configuration configuration) {
        final Object conversionService =
                configuration.getExecutionAttributes().get(STANDARD_CONVERSION_SERVICE_ATTRIBUTE_NAME);
        if (conversionService == null || (!(conversionService instanceof IStandardConversionService))) {
            throw new TemplateProcessingException(
                    "No Standard Conversion Service has been registered as an execution argument. " +
                    "This is a requirement for using " + StandardExpressions.class.getSimpleName() + ", and might happen " +
                    "if neither the Standard or the SpringStandard dialects have " +
                    "been added to the Template Engine and none of the specified dialects registers an " +
                    "attribute of type " + IStandardConversionService.class.getName() + " with name " +
                    "\"" + STANDARD_CONVERSION_SERVICE_ATTRIBUTE_NAME + "\"");
        }
        return (IStandardConversionService) conversionService;
    }


}
