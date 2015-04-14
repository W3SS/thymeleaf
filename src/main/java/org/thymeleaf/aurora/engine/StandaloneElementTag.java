/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2014, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.aurora.engine;

import java.io.IOException;
import java.io.Writer;

import org.thymeleaf.aurora.model.IStandaloneElementTag;
import org.thymeleaf.aurora.templatemode.TemplateMode;
import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
final class StandaloneElementTag
            extends AbstractProcessableElementTag implements IStandaloneElementTag {

    private boolean minimized;



    /*
     * Objects of this class are meant to both be reused by the engine and also created fresh by the processors. This
     * should allow reducing the number of instances of this class to the minimum.
     */


    // Meant to be called only from the template handler adapter
    StandaloneElementTag(
            final TemplateMode templateMode,
            final ElementDefinitions elementDefinitions,
            final AttributeDefinitions attributeDefinitions) {
        super(templateMode, elementDefinitions, attributeDefinitions);
    }



    // Meant to be called only from the model factory
    StandaloneElementTag(
            final TemplateMode templateMode,
            final ElementDefinitions elementDefinitions,
            final AttributeDefinitions attributeDefinitions,
            final String elementName,
            final boolean minimized) {
        super(templateMode, elementDefinitions, attributeDefinitions, elementName);
        this.minimized = minimized;
    }



    // Meant to be called only from the cloneElementTag method
    private StandaloneElementTag() {
        super();
    }




    public boolean isMinimized() {
        return this.minimized;
    }


    public void setMinimized(final boolean minimized) {
        if (this.templateMode.isXML() && !minimized) {
            throw new IllegalArgumentException("Standalone tag cannot be un-minimized when in XML template mode.");
        }
        this.minimized = minimized; // No need to do anything else
    }




    // Meant to be called only from within the engine
    void setStandaloneElementTag(
            final String elementName,
            final boolean minimized,
            final int line, final int col) {

        resetProcessableTag(elementName, line, col);
        this.minimized = minimized;

    }



    // Meant to be called only from within the engine
    void setFromStandaloneElementTag(final IStandaloneElementTag tag) {

        resetProcessableTag(tag.getElementName(), tag.getLine(), tag.getCol());
        this.minimized = tag.isMinimized();
        this.elementAttributes.copyFrom(tag.getAttributes());

    }





    public void write(final Writer writer) throws IOException {
        Validate.notNull(writer, "Writer cannot be null");
        writer.write('<');
        writer.write(this.elementName);
        this.elementAttributes.write(writer);
        if (this.minimized) {
            writer.write("/>");
        } else {
            writer.write('>');
        }
    }





    public StandaloneElementTag cloneElementTag() {
        final StandaloneElementTag clone = new StandaloneElementTag();
        initializeProcessableElementTagClone(clone);
        clone.minimized = this.minimized;
        return clone;
    }

}