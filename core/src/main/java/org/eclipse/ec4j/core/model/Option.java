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

import org.eclipse.ec4j.core.model.optiontypes.OptionException;
import org.eclipse.ec4j.core.model.optiontypes.OptionNames;
import org.eclipse.ec4j.core.model.optiontypes.OptionType;

/**
 * @author <a href="mailto:angelo.zerr@gmail.com">Angelo Zerr</a>
 */
public class Option {

    public static class Builder {

        static boolean isValid(OptionType<?> type, String value) {
            if (type == null) {
                return false;
            }
            try {
                type.validate(value);
            } catch (OptionException e) {
                return false;
            }
            return true;
        }

        /**
         * Return the lowercased option value for certain options.
         *
         * @param name
         * @param value
         * @return the lowercased option value for certain options.
         */
        private static String preprocessOptionValue(OptionNames option, String value) {
            // According test "lowercase_values1" a "lowercase_values2": test that same
            // property values are lowercased (v0.9.0 properties)
            switch (option) {
            case end_of_line:
            case indent_style:
            case indent_size:
            case insert_final_newline:
            case trim_trailing_whitespace:
            case charset:
                return value.toLowerCase();
            default:
                return value;
            }
        }

        private String name;

        private final Section.Builder parentBuilder;
        private String value;
        private Object parsedValue;

        public Builder(org.eclipse.ec4j.core.model.Section.Builder parentBuilder) {
            super();
            this.parentBuilder = parentBuilder;
        }

        boolean checkMax() {
            if (name != null && name.length() > 50) {
                return false;
            }
            if (value != null && value.length() > 255) {
                return false;
            }
            return true;
        }

        public Section.Builder closeOption() {
            if (checkMax()) {
                Option option = new Option(type, name, value, parsedValue, valid);
                parentBuilder.option(option);
            }
            return parentBuilder;
        }

        private OptionType<?> type;

        private boolean valid;

        public Builder name(String name) {
            this.name = name;
            type = parentBuilder.parentBuilder.registry.getType(name);
            return this;
        }

        public Builder value(String value) {
            this.value = preprocessOptionValue(OptionNames.get(name), value);
            this.valid = isValid(type, value);
            if (valid) {
                this.parsedValue = type.parse(value);
            }
            return this;
        }
    }

    private final String name;
    private final Object parsedValue;
    private final String sourceValue;
    private final OptionType<?> type;
    private final boolean valid;

    public Option(OptionType<?> type, String name, String sourceValue, Object parsedValue, boolean valid) {
        super();
        this.type = type;
        this.name = name;
        this.sourceValue = sourceValue;
        this.parsedValue = parsedValue;
        this.valid = valid;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the value
     */
    public String getSourceValue() {
        return sourceValue;
    }

    public OptionType<?> getType() {
        return type;
    }

    public <T> T getValueAs() {
        return (T) parsedValue;
    }

    public boolean isValid() {
        return valid;
    }

    @Override
    public String toString() {
        return new StringBuilder(name).append(" = ").append(sourceValue).toString();
    }

}
