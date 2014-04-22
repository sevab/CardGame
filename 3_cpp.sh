echo './test/cardgame/testDeck.txt' | pbcopy;
ant jar -q;
java -cp ./build/classes/ cardgame.CardGame 100 3
