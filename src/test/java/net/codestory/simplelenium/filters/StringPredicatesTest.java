/**
 * Copyright (C) 2013-2015 all@code-story.net
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */
package net.codestory.simplelenium.filters;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.regex.Pattern;

import com.google.common.base.Predicate;
import org.junit.Test;

public class StringPredicatesTest {
  @Test
  public void match_whole_word() {
    Predicate<String> pattern = StringPredicates.containsWord("word");

    assertThat(pattern.apply("word")).isTrue();
    assertThat(pattern.apply("before word after")).isTrue();
    assertThat(pattern.apply("noword")).isFalse();
  }
  
  @Test
  public void predicate_is_empty() {
    Predicate<String> result = StringPredicates.isEmpty();

    assertThat(result.apply(null)).isTrue();
    assertThat(result.apply("")).isTrue();
    assertThat(result.apply("notEmpty")).isFalse();
  }
  
  @Test
  public void predicate_is_null() {
    Predicate<String> result = StringPredicates.isNull();

    assertThat(result.apply(null)).isTrue();
    assertThat(result.apply("notNull")).isFalse();
  }
  
  @Test
  public void predicate_equals_to() {
    Predicate<String> result = StringPredicates.equalsTo("something");

    assertThat(result.apply("something")).isTrue();
    assertThat(result.apply("anything")).isFalse();
  }
  
  @Test
  public void predicate_contains() {
    Predicate<String> result = StringPredicates.contains("contains");

    assertThat(result.apply("contains")).isTrue();
    assertThat(result.apply("containsAndContainsAgain")).isTrue();
    assertThat(result.apply("cake")).isFalse();
  }
  
  @Test
  public void predicate_contains_with_regex() {
    Pattern regex = Pattern.compile("\\w");
    
    Predicate<String> result = StringPredicates.contains(regex);

    assertThat(result.apply("abcde")).isTrue();
    assertThat(result.apply("!@#$%")).isFalse();
  }
  
  @Test
  public void predicate_starts_with() {
    
    Predicate<String> result = StringPredicates.startsWith("start");

    assertThat(result.apply("startWith")).isTrue();
    assertThat(result.apply("endWith")).isFalse();
  }
  
  @Test
  public void predicate_ends_with() {
    
    Predicate<String> result = StringPredicates.endsWith("end");

    assertThat(result.apply("trend")).isTrue();
    assertThat(result.apply("endurence")).isFalse();
  }
  
  @Test
  public void predicate_matches() {
    Pattern regex = Pattern.compile(".*is.*");
    
    Predicate<String> result = StringPredicates.matches(regex);

    assertThat(result.apply("it is simple")).isTrue();
    assertThat(result.apply("they are complex")).isFalse();
    
  }
}
