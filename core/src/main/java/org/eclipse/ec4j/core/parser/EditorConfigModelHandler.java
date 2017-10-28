/**
 * Copyright (c) 2017 Angelo Zerr and other contributors as
 * indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.eclipse.ec4j.core.parser;

import org.eclipse.ec4j.core.model.EditorConfig;
import org.eclipse.ec4j.core.model.Option;
import org.eclipse.ec4j.core.model.Section;
import org.eclipse.ec4j.core.model.optiontypes.OptionTypeRegistry;

/**
 * @author <a href="mailto:angelo.zerr@gmail.com">Angelo Zerr</a>
 */
public class EditorConfigModelHandler implements EditorConfigHandler {

    private final EditorConfig.Builder editorConfigBuilder;
    private Section.Builder sectionBuilder;
    private Option.Builder optionBuilder;

    public EditorConfigModelHandler(OptionTypeRegistry registry, String version) {
        this.editorConfigBuilder = EditorConfig.builder(registry).version(version);
    }

    @Override
    public void startDocument(ParseContext context) {
        editorConfigBuilder.resourcePath(context.getResource().getParent());
    }

    @Override
    public void endDocument(ParseContext context) {
    }

    @Override
    public void startSection(ParseContext context) {
        sectionBuilder = editorConfigBuilder.openSection();
    }

    @Override
    public void endSection(ParseContext context) {
        sectionBuilder.closeSection();
        sectionBuilder = null;
    }

    @Override
    public void startOption(ParseContext context) {
        optionBuilder = sectionBuilder.openOption();
    }

    @Override
    public void endOption(ParseContext context) {
        optionBuilder.closeOption();
        optionBuilder = null;
    }

    @Override
    public void error(ParseException e) {
        e.printStackTrace();
    }

    public EditorConfig getEditorConfig() {
        return editorConfigBuilder.build();
    }

    @Override
    public void startPattern(ParseContext context) {
        // TODO Auto-generated method stub

    }

    @Override
    public void endPattern(ParseContext context, String pattern) {
        sectionBuilder.pattern(pattern);
    }

    @Override
    public void startOptionName(ParseContext context) {
    }

    @Override
    public void endOptionName(ParseContext context, String name) {
        optionBuilder.name(name);
    }

    @Override
    public void startOptionValue(ParseContext context) {
    }

    @Override
    public void endOptionValue(ParseContext context, String value) {
        optionBuilder.value(value);
    }

}