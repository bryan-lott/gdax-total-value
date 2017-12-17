# gdax-total-value

Get the total value of all cryptocurrencies currently held in USD in a given account.

Note: this is an alpha-may-eat-your-kittens type of project.

## Usage

1. Clone this project `git clone https://github.com/bryan-lott/gdax-total-value.git`
1. Set the following environment variables to your API key:

```bash
export CB_ACCESS_PASSPHRASE='your-passphrase-goes-here'
export CB_ACCESS_KEY='your-key-goes-here'
export CB_ACCESS_SECRET='your-secret-goes-here'
```

1. Run `lein run`

Note: you should probably create a bash script to do the above, it's what I did ;)

## License

Copyright (C) 2017 Bryan Lott

Distributed under the Eclipse Public License, the same as Clojure.

## Attribution

At least some of this code (specifically the authentication with Gdax) was taken
from or inspired by https://github.com/weissjeffm/gdax-bot.  Really nifty bot (that
I don't entirely understand yet) that you should check out.

## Requirements

* Gdax API key with the `View` permission.
* Java 1.8+

## TODO

* Tests!
* GUI?
  * Graphs
* Gain/loss over a configurable time period
* Configurable polling time
