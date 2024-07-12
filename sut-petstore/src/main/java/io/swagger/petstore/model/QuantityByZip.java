/**
 * Copyright 2018 SmartBear Software
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
 * limitations under the License.
 */

package io.swagger.petstore.model;

//Added model to represent the a summary of quantities by zip

public class QuantityByZip {
    private String zip;
    private long total;

	public QuantityByZip(String zip, long total) {
		this.zip = zip;
		this.total = total;
	}

	public String getZip() {
        return zip;
    }

    public void setZip(final String zip) {
        this.zip = zip;
    }

    public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

}
