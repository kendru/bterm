# bterm

A simplistic terminal emulator HTML component

## Overview

This component was designed to accompany the book _Learn ClojureScript_ as a
way to teach simple concepts such as control structures without requiring the
reader to already know about DOM manipulation, event handling, etc.

## Usage

```
(ns myapp
  (:require [bterm.core :as bterm]
            [bterm.io :as io]))

;; Attach component to the DOM
(def terminal (bterm/attach (.getElementById js/document "terminal")
                            {:font-size 14
                             :prompt "$ "}))

;; Print text to the terminal
(io/println terminal "Hello there, fine person. What do you say?")

;; Handle input
(io/read terminal
  (fn [input]
    (io/println terminal "Thank you for saying, \"%s\"" input)))
```

## Setup

To get an interactive development environment run:

    lein figwheel

and open your browser at [localhost:3449](http://localhost:3449/).
This will auto compile and send all changes to the browser without the
need to reload. After the compilation process is complete, you will
get a Browser Connected REPL. An easy way to try it is:

    (js/alert "Am I connected?")

and you should see an alert in the browser window.

To clean all compiled files:

    lein clean

To create a production build run:

    lein do clean, cljsbuild once min

And open your browser in `resources/public/index.html`. You will not
get live reloading, nor a REPL. 

## License

Copyright © 2016 Andrew Meredith

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.
