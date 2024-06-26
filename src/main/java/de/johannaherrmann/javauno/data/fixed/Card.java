package de.johannaherrmann.javauno.data.fixed;

import java.io.Serializable;
import java.util.UUID;

public final class Card implements Serializable {
    private static final long serialVersionUID = 1L;

    private CardType cardType;
    private String color;
    private int value;
    private boolean numberCard;
    private boolean jokerCard;
    private boolean drawCard;
    private int drawValue;
    private final String uuid;

    private Card(){
        this.uuid = UUID.randomUUID().toString();
    }

    private Card(CardType cardType, Color color, int value){
        this();
        this.cardType = cardType;
	    this.color = color.name();
        this.value = value;
    }

    private Card(CardType cardType, Color color){
        this();
        this.cardType = cardType;
        this.color = color.name();
        this.value = 20;
    }

    private Card(CardType cardType){
        this();
        this.cardType = cardType;
        this.value = 50;
        this.color = "joker";
    }

    static Card createNumberCard(Color color, int value){
        value = Math.abs(value) % 10;
	    return new Card(CardType.NUMBER, color, value);
    }

    static Card createSkipCard(Color color){
        return new Card(CardType.SKIP, color);
    }

    static Card createReverseCard(Color color){
        return new Card(CardType.REVERSE, color);
    }

    static Card createDraw2Card(Color color){
        return new Card(CardType.DRAW_2, color);
    }

    static Card createDraw4Card(){
        return new Card(CardType.DRAW_4);
    }

    static Card createJokerCard(){
        return new Card(CardType.JOKER);
    }

    public String getColor() {
        return color;
    }

    public CardType getCardType(){
        return cardType;
    }

    public int getValue() {
        return value;
    }

    public boolean isNumberCard() {
        return CardType.NUMBER.equals(cardType);
    }

    public boolean isJokerCard() {
        return CardType.JOKER.equals(cardType) || CardType.DRAW_4.equals(cardType);
    }

    public boolean isDrawCard() {
        return CardType.DRAW_2.equals(cardType) || CardType.DRAW_4.equals(cardType);
    }

    public int getDrawValue() {
        if(CardType.DRAW_2.equals(cardType)){
            return 2;
        }
        if(CardType.DRAW_4.equals(cardType)){
            return 4;
        }
        return 0;
    }

    public String getUuid() {
        return uuid;
    }

    @Override
    public String toString(){
	    String str = cardType.toString();
	    if(!isJokerCard()){
	        str += ":" + color;
        }
        if(isNumberCard()){
            str += ":" + value;
        }
	    return str;
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof Card){
            return this.toString().equals(o.toString());
        }
        return false;
    }
}
