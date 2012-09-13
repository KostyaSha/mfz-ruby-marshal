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
import java.util.Map;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * 
 * 
 * @author mfizz
 */
public class UnmarshalTest {
    
    static public byte[] hextobytes(String hex) throws DecoderException {
        return Hex.decodeHex(hex.toCharArray());
    }
    
    @Test
    public void unmarshalNil() throws Exception {
        // ruby: Marshal.dump(nil).unpack('H*')
        byte[] bytes = hextobytes("040830");
        RubyType rt = Marshal.load(bytes);
        Assert.assertNull(rt);
    }
    
    @Test
    public void unmarshalFixnum() throws Exception {
        // ruby: Marshal.dump(12345678).unpack('H*')
        byte[] bytes = hextobytes("040869034e61bc");
        RubyFixnum rt = (RubyFixnum)Marshal.load(bytes);
        Assert.assertEquals(12345678, rt.getValue());
    }
    
    @Test
    public void unmarshalBignum() throws Exception {
        // ruby: Marshal.dump(12345678910111212333).unpack('H*')
        byte[] bytes = hextobytes("04086c2b092de335fc8ea954ab");
        RubyBignum rt = (RubyBignum)Marshal.load(bytes);
        Assert.assertEquals(new BigInteger("12345678910111212333"), rt.getValue());
    }
    
    /**
    irb(main):024:0> Marshal.dump(true).unpack('H*')
    => ["040854"]
    */
    @Test
    public void unmarshalBooleanTrue() throws Exception {
        byte[] bytes = hextobytes("040854");
        RubyBoolean rt = (RubyBoolean)Marshal.load(bytes);
        Assert.assertTrue(rt.getValue());
    }
    
    /**
    irb(main):025:0> Marshal.dump(false).unpack('H*')
    => ["040846"]
    */
    @Test
    public void unmarshalBooleanFalse() throws Exception {
        byte[] bytes = hextobytes("040846");
        RubyBoolean rt = (RubyBoolean)Marshal.load(bytes);
        Assert.assertFalse(rt.getValue());
    }
    
    /**
    irb(main):005:0> Marshal.dump("hello world").unpack('H*')
    => ["0408221068656c6c6f20776f726c64"]
    */
    @Test
    public void unmarshalString() throws Exception {
        byte[] bytes = hextobytes("0408221068656c6c6f20776f726c64");
        RubyString rt = (RubyString)Marshal.load(bytes);
        Assert.assertArrayEquals("hello world".getBytes(), rt.getBytes());
    }
    
    /**
    irb(main):004:0> Marshal.dump(12345678.1).unpack('H*')
    => ["0408661231323334353637382e31003333"]
    */
    //@Test
    public void unmarshalFloat() throws Exception {
        // ruby: Marshal.dump(12345678.1).unpack('H*')
        byte[] bytes = hextobytes("0408661231323334353637382e31003333");
        RubyType rt = Marshal.load(bytes);
        Assert.assertTrue(rt instanceof RubyBignum);
        Assert.assertEquals(new BigInteger("12345678910111212333"), ((RubyBignum)rt).getValue());
    }
    
    /**
    irb(main):021:0> Marshal.dump(:testsym).unpack('H*')
    => ["04083a0c7465737473796d"]
    */
    @Test
    public void unmarshalSymbol() throws Exception {
        byte[] bytes = hextobytes("04083a0c7465737473796d");
        RubySymbol rt = (RubySymbol)Marshal.load(bytes);
        Assert.assertEquals("testsym", rt.getName());
        // make sure "testsym" string and this aren't equal
        Assert.assertFalse(rt.equals("testsym"));
    }
    
    /**
    irb(main):022:0> Marshal.dump({"hello" => "world", :testsym => 1, "testsym" => 2}).unpack('H*')
    => ["04087b08220a68656c6c6f220a776f726c64220c7465737473796d69073a0c7465737473796d6906"]
    */
    @Test
    public void unmarshalHash() throws Exception {
        byte[] bytes = hextobytes("04087b08220a68656c6c6f220a776f726c64220c7465737473796d69073a0c7465737473796d6906");
        RubyHash rt = (RubyHash)Marshal.load(bytes);
        
        System.out.println(Ruby.toString(rt));
        
        Assert.assertEquals(3, rt.size());
        
        RubyString key1 = new RubyString("hello".getBytes());
        RubyString value1 = (RubyString)rt.get(key1);
        Assert.assertArrayEquals("world".getBytes(), value1.getBytes());
        
        RubySymbol key2 = new RubySymbol("testsym");
        RubyFixnum value2 = (RubyFixnum)rt.get(key2);
        Assert.assertEquals(1, value2.getValue());
        
        RubyString key3 = new RubyString("testsym".getBytes());
        RubyFixnum value3 = (RubyFixnum)rt.get(key3);
        Assert.assertEquals(2, value3.getValue());
    }
    
    /**
    irb(main):023:0> Marshal.dump(["hello", "world", :testsym, 1, "testsym", 2]).unpack('H*')
    => ["04085b0b220a68656c6c6f220a776f726c643a0c7465737473796d6906220c7465737473796d6907"]
    */
    @Test
    public void unmarshalArray() throws Exception {
        byte[] bytes = hextobytes("04085b0b220a68656c6c6f220a776f726c643a0c7465737473796d6906220c7465737473796d6907");
        RubyArray rt = (RubyArray)Marshal.load(bytes);
        
        Assert.assertEquals(6, rt.size());
        
        Assert.assertArrayEquals("hello".getBytes(), ((RubyString)rt.get(0)).getBytes());
        Assert.assertArrayEquals("world".getBytes(), ((RubyString)rt.get(1)).getBytes());
        Assert.assertEquals("testsym", ((RubySymbol)rt.get(2)).getName());
        Assert.assertEquals(1, ((RubyFixnum)rt.get(3)).getValue());
        Assert.assertArrayEquals("testsym".getBytes(), ((RubyString)rt.get(4)).getBytes());
        Assert.assertEquals(2, ((RubyFixnum)rt.get(5)).getValue());
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
    public void unmarshalObject() throws Exception {
        byte[] bytes = hextobytes("04086f3a0a5465737441063a0a406e616d65221535353631373839306462363836386134");
        RubyObject rt = (RubyObject)Marshal.load(bytes);
        
        Assert.assertEquals(1, rt.count());
        Assert.assertEquals("TestA", rt.getName().getName());
        
        RubyString name = (RubyString)rt.get(new RubySymbol("@name"));
        Assert.assertArrayEquals("55617890db6868a4".getBytes(), name.getBytes());
    }
    
    
    
}
