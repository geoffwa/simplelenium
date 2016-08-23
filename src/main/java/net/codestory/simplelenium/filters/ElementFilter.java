/**
 * Copyright (C) 2013-2015 all@code-story.net
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */
package net.codestory.simplelenium.filters;

import com.google.common.base.Function;
import org.openqa.selenium.WebElement;

class ElementFilter {
    private static final Function<Iterable<WebElement>, Iterable<WebElement>> IDENTITY = new Function<Iterable<WebElement>, Iterable<WebElement>>() {
        @Override
        public Iterable<WebElement> apply(final Iterable<WebElement> iterable) {
            return iterable;
        }
    };

    private static final ElementFilter ANY = new ElementFilter("", IDENTITY);

    private final String description;
    private final Function<Iterable<WebElement>, Iterable<WebElement>> filter;

    ElementFilter(String description, Function<Iterable<WebElement>, Iterable<WebElement>> filter) {
        this.description = description;
        this.filter = filter;
    }

    public String getDescription() {
        return description;
    }

    public Function<Iterable<WebElement>, Iterable<WebElement>> getFilter() {
        return filter;
    }

    public static ElementFilter any() {
        return ANY;
    }

    public ElementFilter and(final ElementFilter second) {
        if (ANY == this) {
            return second;
        }
        if (ANY == second) {
            return this;
        }
        return new ElementFilter(description + ',' + second.description, new Function<Iterable<WebElement>, Iterable<WebElement>>() {
            @Override
            public Iterable<WebElement> apply(final Iterable<WebElement> iterable) {
                return second.filter.apply(filter.apply(iterable));
            }
        });
    }
}
