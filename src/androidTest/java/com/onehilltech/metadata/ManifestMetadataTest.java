/**
 * Copyright (C) 2013, James H. Hill
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

package com.onehilltech.metadata;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.os.Bundle;

import com.onehilltech.metadata.test.TestActivity;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

@RunWith (AndroidJUnit4.class)
public class ManifestMetadataTest
{
  public static final String METADATA_STRING = "metadata.string";
  public static final String METADATA_INTEGER = "metadata.integer";
  public static final String METADATA_CLASSNAME = "metadata.classname";
  
  public static final String METADATA_RESOURCE_STRING = "metadata.resource.string";
  public static final String METADATA_RESOURCE_INTEGER = "metadata.resource.integer";

  public static final String METADATA_RESOURCE_BOOLEAN_TRUE = "metadata.resource.boolean.true";
  public static final String METADATA_RESOURCE_BOOLEAN_FALSE = "metadata.resource.boolean.false";
  public static final String METADATA_RESOURCE_DIMENSION = "metadata.resource.dimension";
  public static final String METADATA_RESOURCE_COLOR = "metadata.resource.color";

  @Test
  public void testGetMetadata ()
  {
    try
    {
      Bundle metadata = ManifestMetadata.get (InstrumentationRegistry.getInstrumentation ().getContext ()).getMetadata ();
      
      Assert.assertEquals (true, metadata.containsKey (METADATA_STRING));
      Assert.assertEquals (true, metadata.containsKey (METADATA_INTEGER));
      Assert.assertEquals (true, metadata.containsKey (METADATA_CLASSNAME));
      
      Assert.assertEquals (true, metadata.containsKey (METADATA_RESOURCE_STRING));
      Assert.assertEquals (true, metadata.containsKey (METADATA_RESOURCE_INTEGER));

      Assert.assertEquals (true, metadata.containsKey (METADATA_RESOURCE_BOOLEAN_TRUE));
      Assert.assertEquals (true, metadata.containsKey (METADATA_RESOURCE_BOOLEAN_FALSE));
      
      Assert.assertEquals (true, metadata.containsKey (METADATA_RESOURCE_DIMENSION));

      Assert.assertEquals (true, metadata.containsKey (METADATA_RESOURCE_COLOR));
    }
    catch (NameNotFoundException e)
    {
      Assert.fail (e.getMessage ());
    }    
  }

  @Test
  public void testGetMetadataValue ()
  {
    try
    {
      ManifestMetadata metadata = ManifestMetadata.get (InstrumentationRegistry.getInstrumentation ().getContext ());
      String value = metadata.getValue (METADATA_STRING);
      Assert.assertEquals ("Hello, World!", value);
      
      int intValue = metadata.getValue (METADATA_INTEGER, Integer.class);
      Assert.assertEquals (42, intValue);
    }
    catch (Exception e)
    {
      Assert.fail (e.getMessage ());
    }    
  }

  @Test
  public void testLoadFromManifest ()
  {
    try
    {
      // Test @Metadata
      
      MetadataValues values = new MetadataValues ();
      ManifestMetadata.get (InstrumentationRegistry.getInstrumentation ().getContext ()).initFromMetadata (values);
      Resources r = InstrumentationRegistry.getInstrumentation ().getContext ().getResources ();

      Assert.assertEquals ("Hello, World!", values.theString);
      Assert.assertEquals (42, values.theInteger);
      Assert.assertEquals (TestClass.class, values.theClass);

      Assert.assertEquals ("Hello, World!", values.theStringResource);
      Assert.assertEquals (com.onehilltech.metadata.test.R.string.hello_world, values.theIntegerResource);

      Assert.assertTrue (values.theTrueValue);    
      Assert.assertFalse (values.theFalseValue);
      
      Assert.assertEquals (r.getDimension (com.onehilltech.metadata.test.R.dimen.sample_dimen), values.theDimension);
      
      // Test @MetadataMethod
      Assert.assertEquals ("Hello, World!", values.getMetadataString ());
    }
    catch (Exception e)
    {
      e.printStackTrace ();
      Assert.fail (e.getMessage ());
    }    
  }

  @Test
  public void testLoadFromManifestWithResourceType ()
  {
    try
    {
      MetadataValues values = new MetadataValues ();
      ManifestMetadata.get (InstrumentationRegistry.getInstrumentation ().getContext ()).initFromMetadata (values);
      Resources r =InstrumentationRegistry.getInstrumentation ().getContext ().getResources ();
      
      // Testing the resourceType method.
      Assert.assertEquals (r.getColor (com.onehilltech.metadata.test.R.color.black), values.colorBlack);
    }
    catch (Exception e)
    {
      e.printStackTrace ();
      Assert.fail (e.getMessage ());
    }        
  }

  @Test(expected=NameNotFoundException.class)
  public void testNameNotFoundException () throws Exception
  {
    ManifestMetadata metadata = ManifestMetadata.get (InstrumentationRegistry.getInstrumentation ().getContext ());
    metadata.getValue ("foo", String.class);
  }

  @Test
  public void testActivityMetadata () throws Exception
  {
    Context context = InstrumentationRegistry.getInstrumentation ().getContext ();
    ComponentName componentName = new ComponentName (context, TestActivity.class);
    ManifestMetadata metadata = ManifestMetadata.get (context, componentName);

    Assert.assertEquals (1, metadata.getMetadata ().size ());
    Assert.assertEquals ("Hello, World", metadata.getValue ("metadata.activity.string"));
  }
}
