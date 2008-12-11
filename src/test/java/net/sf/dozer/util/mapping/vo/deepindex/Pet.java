/*
 * Copyright 2005-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.sf.dozer.util.mapping.vo.deepindex;

import net.sf.dozer.util.mapping.vo.BaseTestObject;

public class Pet extends BaseTestObject {
  private String petName;
  private int petAge;
  private Pet[] offSpring;

  public Pet() {
  }

  public Pet(String petName, int petAge, Pet[] offSpring) {
    this.petName = petName;
    this.petAge = petAge;
    this.offSpring = offSpring;
  }

  public Pet[] getOffSpring() {
    return offSpring;
  }
  public void setOffSpring(Pet[] offSpring) {
    this.offSpring = offSpring;
  }
  public int getPetAge() {
    return petAge;
  }
  public void setPetAge(int petAge) {
    this.petAge = petAge;
  }
  public String getPetName() {
    return petName;
  }
  public void setPetName(String petName) {
    this.petName = petName;
  }
}