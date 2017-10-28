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
package org.eclipse.ec4j.core.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.ec4j.core.model.optiontypes.OptionNames;
import org.eclipse.ec4j.core.model.optiontypes.OptionType;
import org.eclipse.ec4j.core.model.optiontypes.OptionTypeRegistry;

/**
 * A section in an {@code .editorconfig} file.
 *
 * @author <a href="mailto:angelo.zerr@gmail.com">Angelo Zerr</a>
 */
public class Section {

    public static class Builder {

        private Glob glob;

        private Map<String, Option> options = new LinkedHashMap<>();
        final EditorConfig.Builder parentBuilder;

        public Builder(org.eclipse.ec4j.core.model.EditorConfig.Builder parentBuilder) {
            super();
            this.parentBuilder = parentBuilder;
        }

        public Section build() {
            preprocessOptions();
            return new Section(glob, Collections.unmodifiableList(new ArrayList<Option>(options.values())));
        }

        public EditorConfig.Builder closeSection() {
            if (glob == null) {
                /* this is the first glob-less section */
                Option rootOption = options.remove("root");
                if (rootOption != null) {
                    parentBuilder.root(rootOption.getSourceValue().equalsIgnoreCase(Boolean.TRUE.toString()));
                }
            } else {
                parentBuilder.section(build());
            }
            return parentBuilder;
        }
        public Option.Builder openOption() {
            return new Option.Builder(this);
        }

        public Builder option(Option option) {
            this.options.put(option.getName(), option);
            return this;
        }

        public Builder options(Collection<Option> options) {
            for (Option option : options) {
                this.options.put(option.getName(), option);
            }
            return this;
        }

        public Builder options(Option... options) {
            for (Option option : options) {
                this.options.put(option.getName(), option);
            }
            return this;
        }

        public Builder pattern(String pattern) {
            this.glob = new Glob(parentBuilder.resourcePath.getPath(), pattern);
            return this;
        }

        private void preprocessOptions() {
            String version = parentBuilder.version;
            Option indentStyle = null;
            Option indentSize = null;
            Option tabWidth = null;
            for (Option option : options.values()) {
                OptionNames name = OptionNames.get(option.getName());
                // Lowercase option value for certain options
                // get indent_style, indent_size, tab_width option
                switch (name) {
                case indent_style:
                    indentStyle = option;
                    break;
                case indent_size:
                    indentSize = option;
                    break;
                case tab_width:
                    tabWidth = option;
                    break;
                default:
                    break;
                }
            }

            // Set indent_size to "tab" if indent_size is unspecified and
            // indent_style is set to "tab".
            if (indentStyle != null && "tab".equals(indentStyle.getSourceValue()) && indentSize == null
                    && RegexpUtils.compareVersions(version, "0.10.0") >= 0) {
                final String name = OptionNames.indent_size.name();
                final OptionType<?> type = parentBuilder.registry.getType(name);
                final String value = "tab";
                indentSize = new Option(type, name, value, type.parse(value), true);
                this.option(indentSize);
            }

            // Set tab_width to indent_size if indent_size is specified and
            // tab_width is unspecified
            if (indentSize != null && !"tab".equals(indentSize.getSourceValue()) && tabWidth == null) {
                final String name = OptionNames.tab_width.name();
                final OptionType<?> type = parentBuilder.registry.getType(name);
                final String value = indentSize.getSourceValue();
                tabWidth = new Option(type, name, value, type.parse(value), true);
                this.option(tabWidth);
            }

            // Set indent_size to tab_width if indent_size is "tab"
            if (indentSize != null && "tab".equals(indentSize.getSourceValue()) && tabWidth != null) {
                final String name = OptionNames.indent_size.name();
                final OptionType<?> type = parentBuilder.registry.getType(name);
                final String value = tabWidth.getSourceValue();
                indentSize = new Option(type, name, value, type.parse(value), true);
                this.option(indentSize);
            }
        }

    }

    private final Glob glob;

    private final List<Option> options;

    /**
     * You look for {@link #builder(OptionTypeRegistry)} if you wonder why this constructor this package private.
     * @param glob
     * @param options
     */
    Section(Glob glob, List<Option> options) {
        super();
        this.glob = glob;
        this.options = options;
    }
    public void appendTo(StringBuilder s) {
        // patterns
        if (!glob.isEmpty()) {
            s.append('[');
            s.append(glob.toString());
            s.append("]\n");
        }
        // options
        int i = 0;
        for (Option option : options) {
            if (i > 0) {
                s.append("\n");
            }
            s.append(option.toString());
            i++;
        }
    }

    public Glob getGlob() {
        return glob;
    }

    public List<Option> getOptions() {
        return options;
    }

    public boolean match(String filePath) {
        /* null glob matches all */
        return glob == null ? true : glob.match(filePath);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        appendTo(s);
        return s.toString();
    }
}
