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
package com.mfizz.ruby.marshal;

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
import com.mfizz.ruby.types.RubyArray;
import com.mfizz.ruby.types.RubyBignum;
import com.mfizz.ruby.types.RubyBoolean;
import com.mfizz.ruby.types.RubyFixnum;
import com.mfizz.ruby.types.RubyHash;
import com.mfizz.ruby.types.RubyObject;
import com.mfizz.ruby.types.RubyString;
import com.mfizz.ruby.types.RubySymbol;
import com.mfizz.ruby.types.RubyType;
import java.math.BigInteger;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.junit.Assert;
import org.junit.Test;

/**
 * 
 * 
 * @author mfizz
 */
public class MarshalTest {
    
    static public byte[] hextobytes(String hex) throws DecoderException {
        return Hex.decodeHex(hex.toCharArray());
    }
    
    @Test
    public void marshalNil() throws Exception {
        byte[] actualBytes = Marshal.dump(null);
        
        // ruby: Marshal.dump(nil).unpack('H*')
        byte[] bytes = hextobytes("040830");
        
        Assert.assertArrayEquals(bytes, actualBytes);
    }
    
    @Test
    public void marshalFixnum() throws Exception {
        byte[] actualBytes = Marshal.dump(new RubyFixnum(12345678));
        
        // ruby: Marshal.dump(12345678).unpack('H*')
        byte[] bytes = hextobytes("040869034e61bc");
        
        Assert.assertArrayEquals(bytes, actualBytes);
    }
    
    @Test
    public void marshalBignum() throws Exception {
        byte[] actualBytes = Marshal.dump(new RubyBignum(new BigInteger("12345678910111212333")));
        
        // ruby: Marshal.dump(12345678910111212333).unpack('H*')
        byte[] bytes = hextobytes("04086c2b092de335fc8ea954ab");
        
        Assert.assertArrayEquals(bytes, actualBytes);
    }
    
    /**
    irb(main):024:0> Marshal.dump(true).unpack('H*')
    => ["040854"]
    */
    @Test
    public void marshalBooleanTrue() throws Exception {
        byte[] actualBytes = Marshal.dump(new RubyBoolean(true));
        
        byte[] bytes = hextobytes("040854");
        
        Assert.assertArrayEquals(bytes, actualBytes);
    }
    
    /**
    irb(main):025:0> Marshal.dump(false).unpack('H*')
    => ["040846"]
    */
    @Test
    public void marshalBooleanFalse() throws Exception {
        byte[] actualBytes = Marshal.dump(new RubyBoolean(false));
        
        byte[] bytes = hextobytes("040846");
        
        Assert.assertArrayEquals(bytes, actualBytes);
    }
    
    /**
    irb(main):005:0> Marshal.dump("hello world").unpack('H*')
    => ["0408221068656c6c6f20776f726c64"]
    */
    @Test
    public void marshalString() throws Exception {
        byte[] actualBytes = Marshal.dump(new RubyString("hello world".getBytes()));
        
        byte[] bytes = hextobytes("0408221068656c6c6f20776f726c64");
        
        Assert.assertArrayEquals(bytes, actualBytes);
    }
    
    /**
    irb(main):004:0> Marshal.dump(12345678.1).unpack('H*')
    => ["0408661231323334353637382e31003333"]
    */
    //@Test
    public void marshalFloat() throws Exception {
        // ruby: Marshal.dump(12345678.1).unpack('H*')
        byte[] bytes = hextobytes("0408661231323334353637382e31003333");
        RubyType rt = Marshal.load(bytes);
        Assert.assertTrue(rt instanceof RubyBignum);
        Assert.assertEquals(new BigInteger("12345678910111212333"), ((RubyBignum)rt).getValue());
        
        //Assert.assertArrayEquals(bytes, actualBytes);
    }
    
    /**
    irb(main):021:0> Marshal.dump(:testsym).unpack('H*')
    => ["04083a0c7465737473796d"]
    */
    @Test
    public void marshalSymbol() throws Exception {
        byte[] actualBytes = Marshal.dump(new RubySymbol("testsym"));
        
        byte[] bytes = hextobytes("04083a0c7465737473796d");
        
        Assert.assertArrayEquals(bytes, actualBytes);
    }
    
    /**
    irb(main):022:0> Marshal.dump({"hello" => "world", :testsym => 1, "testsym" => 2}).unpack('H*')
    => ["04087b08220a68656c6c6f220a776f726c64220c7465737473796d69073a0c7465737473796d6906"]
    */
    @Test
    public void marshalHash() throws Exception {
        RubyHash rt = new RubyHash();
        
        RubyString key1 = new RubyString("hello".getBytes());
        RubyString value1 = new RubyString("world".getBytes());
        rt.put(key1, value1);
        
        RubyString key3 = new RubyString("testsym".getBytes());
        RubyFixnum value3 = new RubyFixnum(2);
        rt.put(key3, value3);
        
        RubySymbol key2 = new RubySymbol("testsym");
        RubyFixnum value2 = new RubyFixnum(1);
        rt.put(key2, value2);
        
        System.out.println(Ruby.toString(rt));
        
        byte[] actualBytes = Marshal.dump(rt);
        
        byte[] bytes = hextobytes("04087b08220a68656c6c6f220a776f726c64220c7465737473796d69073a0c7465737473796d6906");
        
        Assert.assertArrayEquals(bytes, actualBytes);
    }
    
    /**
    irb(main):045:0> Marshal.dump(["hello", "world"]).unpack('H*')
    => ["04085b07220a68656c6c6f220a776f726c64"]
    */
    @Test
    public void marshalArray() throws Exception {
        RubyArray rt = new RubyArray();
        rt.add(new RubyString("hello".getBytes()));
        rt.add(new RubyString("world".getBytes()));
        
        byte[] actualBytes = Marshal.dump(rt);
        
        byte[] bytes = hextobytes("04085b07220a68656c6c6f220a776f726c64");
        
        Assert.assertArrayEquals(bytes, actualBytes);
    }
    
    /**
    irb(main):026:0> class TestA
irb(main):027:1>   attr_accessor :name
irb(main):028:1> end
=> nil
irb(main):029:0> 
irb(main):030:0* e = TestA.new
=> #<TestA:0x7f3083ff68f0>
irb(main):031:0> e.name = "55617890db6868a4"
=> "55617890db6868a4"
irb(main):032:0> 
irb(main):033:0* Marshal.dump(e).unpack('H*')
=> ["04086f3a0a5465737441063a0a406e616d65221535353631373839306462363836386134"]

    */
    @Test
    public void marshalObject() throws Exception {
        RubyObject rt = new RubyObject(new RubySymbol("TestA"));
        rt.put(new RubySymbol("@name"), new RubyString("55617890db6868a4".getBytes()));
        
        byte[] actualBytes = Marshal.dump(rt);
        
        byte[] bytes = hextobytes("04086f3a0a5465737441063a0a406e616d65221535353631373839306462363836386134");
        
        Assert.assertArrayEquals(bytes, actualBytes);
    }
    
    
    
}
