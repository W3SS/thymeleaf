/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2016, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.engine;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.IThrottledTemplateProcessor;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.TemplateSpec;
import org.thymeleaf.context.IEngineContext;
import org.thymeleaf.exceptions.TemplateEngineException;
import org.thymeleaf.exceptions.TemplateOutputException;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.util.LoggingUtils;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 3.0.0
 *
 */
final class ThrottledTemplateProcessor implements IThrottledTemplateProcessor {

    private static final Logger logger = LoggerFactory.getLogger(TemplateEngine.class);
    private static final Logger timerLogger = LoggerFactory.getLogger(TemplateEngine.TIMER_LOGGER_NAME);

    private static final int NANOS_IN_SECOND = 1000000;


    private final TemplateSpec templateSpec;
    private final IEngineContext context;
    private final TemplateModel templateModel;
    private final ITemplateHandler templateHandler;
    private final TemplateFlowController flowController;
    private final ThrottledTemplateWriter writer;

    private boolean eventProcessingFinished;
    private boolean allProcessingFinished;


    public ThrottledTemplateProcessor(
            final TemplateSpec templateSpec,
            final IEngineContext context,
            final TemplateModel templateModel, final ITemplateHandler templateHandler,
            final TemplateFlowController flowController,
            final ThrottledTemplateWriter writer) {
        super();
        this.templateSpec = templateSpec;
        this.context = context;
        this.templateModel = templateModel;
        this.templateHandler = templateHandler;
        this.flowController = flowController;
        this.writer = writer;
        this.eventProcessingFinished = false;
        this.allProcessingFinished = false;
    }




    @Override
    public void allow(final int limitChars) {

        if (!this.allProcessingFinished) {

            this.writer.allow(limitChars);

            this.allProcessingFinished = this.eventProcessingFinished && !this.writer.isOverflowed();

            if (this.allProcessingFinished) {
                if (logger.isTraceEnabled()) {
                    logger.trace(
                            "[THYMELEAF][{}] FINISHED OUTPUT OF THROTTLED TEMPLATE \"{}\" WITH LOCALE {}. MAXIMUM OVERFLOW WAS {} CHARS.",
                            new Object[]{TemplateEngine.threadIndex(), this.templateSpec, this.context.getLocale(), Integer.valueOf(this.writer.getMaxOverflowSize()) });
                }
            }

        }

    }




    public boolean isFinished() {
        return this.allProcessingFinished;
    }




    public void processAll() {

        try {

            if (logger.isTraceEnabled()) {
                logger.trace("[THYMELEAF][{}] STARTING PROCESS-ALL THROTTLED OF TEMPLATE \"{}\" WITH LOCALE {}",
                        new Object[]{TemplateEngine.threadIndex(), this.templateSpec, this.context.getLocale()});
            }

            final long startNanos = System.nanoTime();

            this.templateModel.process(this.templateHandler);
            EngineContextManager.disposeEngineContext(this.context);

            final long endNanos = System.nanoTime();

            if (logger.isTraceEnabled()) {
                logger.trace("[THYMELEAF][{}] FINISHED PROCESS-ALL OF THROTTLED TEMPLATE \"{}\" WITH LOCALE {}",
                        new Object[]{TemplateEngine.threadIndex(), this.templateSpec, this.context.getLocale()});
            }

            if (timerLogger.isTraceEnabled()) {
                final BigDecimal elapsed = BigDecimal.valueOf(endNanos - startNanos);
                final BigDecimal elapsedMs = elapsed.divide(BigDecimal.valueOf(NANOS_IN_SECOND), RoundingMode.HALF_UP);
                timerLogger.trace(
                        "[THYMELEAF][{}][{}][{}][{}][{}] TEMPLATE \"{}\" WITH LOCALE {} PROCESSED (THROTTLED, PROCESS-ALL) IN {} nanoseconds (approx. {}ms)",
                        new Object[]{
                                TemplateEngine.threadIndex(),
                                LoggingUtils.loggifyTemplateName(this.templateSpec.getTemplate()), this.context.getLocale(), elapsed, elapsedMs,
                                this.templateSpec, this.context.getLocale(), elapsed, elapsedMs});
            }

        } catch (final TemplateOutputException e) {

            this.eventProcessingFinished = true;
            this.allProcessingFinished = true;
            // We log the exception just in case higher levels do not end up logging it (e.g. they could simply display traces in the browser
            logger.error(String.format("[THYMELEAF][%s] Exception processing throttled template \"%s\": %s", new Object[] {TemplateEngine.threadIndex(), this.templateSpec, e.getMessage()}), e);
            throw e;

        } catch (final TemplateEngineException e) {

            this.eventProcessingFinished = true;
            this.allProcessingFinished = true;
            // We log the exception just in case higher levels do not end up logging it (e.g. they could simply display traces in the browser
            logger.error(String.format("[THYMELEAF][%s] Exception processing throttled template \"%s\": %s", new Object[] {TemplateEngine.threadIndex(), this.templateSpec, e.getMessage()}), e);
            throw e;

        } catch (final RuntimeException e) {

            this.eventProcessingFinished = true;
            this.allProcessingFinished = true;
            // We log the exception just in case higher levels do not end up logging it (e.g. they could simply display traces in the browser
            logger.error(String.format("[THYMELEAF][%s] Exception processing throttled template \"%s\": %s", new Object[] {TemplateEngine.threadIndex(), this.templateSpec, e.getMessage()}), e);
            throw new TemplateProcessingException("Exception processing throttled template", this.templateSpec.toString(), e);

        }

        this.eventProcessingFinished = true;
        this.allProcessingFinished = this.eventProcessingFinished && !this.writer.isOverflowed();

        if (this.allProcessingFinished) {
            if (logger.isTraceEnabled()) {
                logger.trace(
                        "[THYMELEAF][{}] FINISHED OUTPUT OF THROTTLED TEMPLATE \"{}\" WITH LOCALE {}. MAXIMUM OVERFLOW WAS {} CHARS.",
                        new Object[]{TemplateEngine.threadIndex(), this.templateSpec, this.context.getLocale(), Integer.valueOf(this.writer.getMaxOverflowSize()) });
            }
        }

    }




}