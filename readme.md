# This is a fork
This is a fork of the Bunch repo [here](https://github.com/ArchitectingSoftware/Bunch).

# Why fork?
1. I'm looking for a way to optimally partition a dependency diagram into parts bigger than the individual nodes.
Let's call those parts subsystems. The Bunch repo that this is forked from seems like a great place to start.
Unfortunately it:
   1. isn't well tested. There are some high-level tests, but they aren't in a state where they can be run with a simple pass/fail.
   1. does way more than what I want. It comes with a UI which allows all sorts of customization and even supports a distributed mode. That extra functionality could be awesome if it was wrapped around a well tested interface that I knew how to use and embed.
   1. is not maintained. See the next point. 
1. I haven't been able to contact Brian Mitchell.

My goal with this fork is to extract a small well-tested library that I can use elsewhere. I'm slowly cutting out things I don't intend to use and adding tests as a methodology of producing that library. Perhaps I would have been better off just reading the thesis and writing for scratch.

# Bunch
Bunch is a project that Brian Mitchell built as part of his Ph.D. research in Computer Science at [Drexel University](http://drexel.edu/cci/). More information on the Bunch tool and his research into software clustering can be found on his [webpage](https://www.cs.drexel.edu/~bmitchell/new/#/research)

### Licensing

The source code is licensed under GPL v3. License is available [here](https://www.gnu.org/licenses/gpl-3.0.en.html).
