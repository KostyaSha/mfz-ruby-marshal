/*
 * Copyright 2012 mfizz.
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
package com.mfizz.ruby.demo;

import com.mfizz.ruby.core.Ruby;
import com.mfizz.ruby.marshal.Marshal;
import com.mfizz.ruby.marshal.Unmarshaller;
import com.mfizz.ruby.types.*;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author mfizz
 */
public class UnmarshalMain {
    private static final Logger logger = LoggerFactory.getLogger(UnmarshalMain.class);
    
    static public void main(String[] args) throws Exception {
        // use following code in ruby to test encoding stuff
        // Marshal.dump(counter).unpack('H*')
        
        Unmarshaller parser = new Unmarshaller();
        
        // simple integer in ruby
        // counter = 1
        byte[] itemBytes = Hex.decodeHex("04086906".toCharArray());
        
        RubyType obj = Marshal.load(itemBytes);
        logger.info("unmarshalled: {}", Ruby.toString(obj));
    }
    
}
