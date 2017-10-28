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

/**
 * @author <a href="mailto:angelo.zerr@gmail.com">Angelo Zerr</a>
 */
public interface EditorConfigHandler {

    void startDocument(ParseContext context);

    void endDocument(ParseContext context);

    void startSection(ParseContext context);

    void endSection(ParseContext context);

    void startPattern(ParseContext context);
    void endPattern(ParseContext context, String pattern);

    void startOption(ParseContext context);

    void endOption(ParseContext context);

    void startOptionName(ParseContext context);
    void endOptionName(ParseContext context, String name);

    void startOptionValue(ParseContext context);
    void endOptionValue(ParseContext context, String value);

    void error(ParseException e);

}