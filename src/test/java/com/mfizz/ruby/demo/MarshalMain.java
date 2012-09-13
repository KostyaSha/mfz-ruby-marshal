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

/*
 * #%L
 * mfz-ruby-marshal
 * %%
 * Copyright (C) 2012 mfizz
 * %%
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
 * #L%
 */

import com.mfizz.ruby.core.Ruby;
import com.mfizz.ruby.marshal.Marshal;
import com.mfizz.ruby.types.*;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author mfizz
 */
public class MarshalMain {
    private static final Logger logger = LoggerFactory.getLogger(MarshalMain.class);
    
    static public void main(String[] args) throws Exception {
        // use following code in ruby to test decoding stuff
        // Marshal.load(["040830"].pack('H*')).inspect()
        
        // object w/ no vars
        RubyObject obj = new RubyObject(new RubySymbol("Object"));
        // add some vars
        obj.put(new RubySymbol("@test"), new RubyString("test".getBytes()));

        logger.info("obj to marshal: {}", Ruby.toString(obj));
        
        byte[] bytes = Marshal.dump(obj);
        logger.info("marshal dump: {}", Hex.encodeHexString(bytes));
        
        // try to load it back up
        RubyType obj2 = Marshal.load(bytes);
        logger.info("marshal load: {}", Ruby.toString(obj2));
    }
    
}
