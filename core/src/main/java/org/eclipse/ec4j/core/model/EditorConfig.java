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
import java.util.List;

import org.eclipse.ec4j.core.EditorConfigConstants;
import org.eclipse.ec4j.core.ResourcePaths.ResourcePath;
import org.eclipse.ec4j.core.model.optiontypes.OptionTypeRegistry;

/**
 * A immutable model of an {@code .editorconfig} file.
 *
 * @author <a href="mailto:angelo.zerr@gmail.com">Angelo Zerr</a>
 */
public class EditorConfig {

    /**
     * {@link EditorConfig} builder.
     */
    public static class Builder {
        final OptionTypeRegistry registry;

        ResourcePath resourcePath;
        Boolean root;
        List<Section> sections = new ArrayList<>();
        String version = EditorConfigConstants.VERSION;

        public Builder(OptionTypeRegistry registry) {
            super();
            this.registry = registry;
        }

        public EditorConfig build() {
            return new EditorConfig(root, version, resourcePath, sections);
        }

        public Section.Builder openSection() {
            return new Section.Builder(this);
        }

        public Builder resourcePath(ResourcePath resourcePath) {
            this.resourcePath = resourcePath;
            return this;
        }

        public Builder root(Boolean root) {
            this.root = root;
            return this;
        }

        public Builder section(Section section) {
            this.sections.add(section);
            return this;
        }

        public Builder sections(Collection<Section> sections) {
            this.sections.addAll(sections);
            return this;
        }

        public Builder sections(Section... sections) {
            for (Section section : sections) {
                this.sections.add(section);
            }
            return this;
        }

        public Builder version(String version) {
            this.version = version;
            return this;
        }
    }

    /**
     * @param registry
     *            the {@link OptionTypeRegistry} to use when adding {@link Option}s.
     *
     * @return a new {@link EditorConfig.Builder}
     */
    public static Builder builder(OptionTypeRegistry registry) {
        return new Builder(registry);
    }

    private final ResourcePath resourcePath;

    /**
     * Citing from <a href="http://editorconfig.org/">http://editorconfig.org/</a> : "A search for .editorconfig files
     * will stop if the root filepath is reached or an EditorConfig file with root=true is found."
     * <p>
     * Note that the type of {@link #root} is {@link Boolean} rather than {@code boolean}. A {@code null} {@link #root}
     * means that the `root` property was not available in the file.
     */
    private final Boolean root;

    private final List<Section> sections;

    private final String version;

    /**
     * You look for {@link #builder(OptionTypeRegistry)} if you wonder why this constructor this package private.
     *
     * @param root
     * @param version
     * @param resourcePath
     * @param sections
     */
    EditorConfig(Boolean root, String version, ResourcePath resourcePath, List<Section> sections) {
        super();
        this.root = root;
        this.version = version;
        this.resourcePath = resourcePath;
        this.sections = sections;
    }

    /**
     * @return The directory where the underlying {@code .editorconfig} file resides
     */
    public ResourcePath getResourcePath() {
        return resourcePath;
    }

    /**
     * @return the {@link List} of {@link Section}s
     */
    public List<Section> getSections() {
        return sections;
    }

    /**
     * @return The version of EditorConfig specification, the current {@link EditorConfig} model is compliant with
     */
    public String getVersion() {
        return version;
    }

    /**
     * @return {@code true} if the underlying {@code .editorconfig} file had the {@code root} property specified;
     *         {@code false} otherwise. A shorthand for {@code getRoot() != null}
     *
     */
    public boolean hasRootProperty() {
        return root != null;
    }

    /**
     * @return {@code true} if a search for {@code .editorconfig} files should stop at the present directory level.
     */
    public boolean isRoot() {
        return Boolean.TRUE.equals(root);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        if (root != null) {
            s.append("root = ");
            s.append(root);
            s.append("\n\n");
        }
        int i = 0;
        for (Section section : sections) {
            if (i > 0) {
                s.append("\n\n");
            }
            section.appendTo(s);
            i++;
        }
        return s.toString();
    }
}
