

# This is a fork of Matt Neuro's Rest Server project

# Code written by Matt Neuro is being used under the MIT License, which is included.

# Code written by Karl Bellve is also under the MIT License, which is included.

This is currently being moved to µManager 2.0 code base. This may or may not work at any time due to 
ongoing development. This code no longer works with µManager 1.4 or earlier.


# MMrestServer
A (very) basic **RESTserver** for [MicroManager](https://micro-manager.org/).

## Getting started
There are two ways to use this:

* Use the precompiled .jar directly.
    Download the [/dist/µmKNIME.jar](https://github.com/kbellve/MMrestServer/blob/master/dist/%C2%B5mWeb.jar) and place it in your MicroManager plugins folder. 
    Normally, this would be somewhere like /micro-manager/mmplugins/


Once the plugin is in place, it needs to be activated after MicroManager is started by going to the Plugins->Beta menu option and selecting **µmWeb**. 
No dialogue is currently shown, though the console output should list whether the REST server has become active.

## Consuming the REST service
Once the plugin is active, it will bind to the local address on port 8000. 
A short documentation on how to consume it is then available at [http://localhost:8000/](http://localhost:8000/). 


