package it.xpug.unsprung.hangman.domain;

import it.xpug.unsprung.hangman.util.SetConverter;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Convert;
import javax.persistence.Embeddable;
import java.util.*;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;

@Embeddable
@EqualsAndHashCode
@ToString
public class Prisoner {

	private String word;

	private int guessesRemaining = 18;

    @Convert(converter = SetConverter.class)
	private Set<String> misses = new HashSet<String>();

    @Convert(converter = SetConverter.class)
	private Set<String> hits = new HashSet<String>();

	public Prisoner() {
		this(new WordList().getRandomWord());
	}

	public Prisoner(String word) {
		this.word = word;
	}

	public Map<String, Object> toMap() {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("word", getMaskedWord());
		result.put("guesses_remaining", guessesRemaining);
		result.put("misses", misses);
		result.put("hits", hits);
		result.put("state", state());
		return result;
	}

	private String state() {
		if (word.equals(getMaskedWord() ))
			return "rescued";
		if (guessesRemaining > 0)
			return "help";
		return "lost";
	}

	private String getMaskedWord() {
		String result = "";
		for (int i=0; i<word.length(); i++) {
			String c = word.substring(i, i+1);
			if (hits.contains(c)) {
				result += c;
			} else {
				result += "*";
			}
		}
		return result;
	}

	public void guess(String guess) {
		if (guessesRemaining == 0) {
			return;
		}
		this.guessesRemaining--;
		if (word.contains(guess)) {
			hits.add(guess);
		} else {
			misses.add(guess);
		}
	}

	public int getGuessesRemaining() {
		return guessesRemaining;
	}
}