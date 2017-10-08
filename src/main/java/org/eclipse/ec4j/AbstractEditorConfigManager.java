/**
 * The MIT License
 * Copyright © 2017 Angelo Zerr and other contributors as
 * indicated by the @author tags.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.eclipse.ec4j;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.ec4j.model.EditorConfig;
import org.eclipse.ec4j.model.Option;
import org.eclipse.ec4j.model.Section;
import org.eclipse.ec4j.model.optiontypes.OptionTypeRegistry;

public class AbstractEditorConfigManager<T> {

	private final String configFilename;
	private final String version;
	private final OptionTypeRegistry registry;
	private final ResourceProvider<T> provider;

	/**
	 * Creates EditorConfig handler with default configuration filename
	 * (.editorconfig) and version {@link EditorConfig#VERSION}
	 */
	public AbstractEditorConfigManager(ResourceProvider<T> provider) {
		this(OptionTypeRegistry.DEFAULT, provider, EditorConfigConstants.EDITORCONFIG, EditorConfigConstants.VERSION);
	}

	/**
	 * Creates EditorConfig handler with specified configuration filename and
	 * version. Used mostly for debugging/testing.
	 * 
	 * @param option
	 *            type registry
	 * @param configFilename
	 *            configuration file name to be searched for instead of
	 *            .editorconfig
	 * @param version
	 *            required version
	 */
	public AbstractEditorConfigManager(OptionTypeRegistry registry, ResourceProvider<T> provider, String configFilename,
			String version) {
		this.registry = registry;
		this.configFilename = configFilename;
		this.version = version;
		this.provider = provider;
	}

	public Collection<Option> getOptions(T file, Set<T> explicitRootDirs) throws EditorConfigException {
		Map<String, Option> options = new LinkedHashMap<>();
		try {
			boolean root = false;
			T dir = provider.getParent(file);
			while (dir != null && !root) {
				T configFile = provider.getResource(dir, getConfigFilename());
				if (provider.exists(configFile)) {
					EditorConfig config = EditorConfig.load(configFile, provider, getRegistry(), getVersion());
					root = config.isRoot();
					List<Section> sections = config.getSections();
					for (Section section : sections) {
						if (section.match(provider.getPath(file))) {
							// Section matches the editor file, collect options of the section
							List<Option> o = section.getOptions();
							for (Option option : o) {
								options.put(option.getName(), option);
							}
						}
					}
				}
				root |= explicitRootDirs != null && explicitRootDirs.contains(dir);
				dir = provider.getParent(dir);
			}
		} catch (Exception e) {
			throw new EditorConfigException(null, e);
		}

		return options.values();
	}

	public OptionTypeRegistry getRegistry() {
		return registry;
	}

	public String getVersion() {
		return version;
	}

	public String getConfigFilename() {
		return configFilename;
	}
}