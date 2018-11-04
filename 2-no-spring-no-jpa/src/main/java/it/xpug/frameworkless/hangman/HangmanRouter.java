package it.xpug.frameworkless.hangman;

import it.xpug.frameworkless.hangman.web.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static javax.servlet.http.HttpServletResponse.SC_CREATED;
import static javax.servlet.http.HttpServletResponse.SC_OK;

@Slf4j
public class HangmanRouter {

    private final HangmanController hangmanController;

    public HangmanRouter(HangmanController hangmanController) {
        this.hangmanController = hangmanController;
    }

    public void route(WebRequest webRequest, WebResponse webResponse) throws IOException {
        try {
            doRoute(webRequest, webResponse);
        } catch (ClientError e) {
            webResponse.clientError(e);
        } catch (Exception e) {
            log.error("Internal server error", e);
            webResponse.serverError(e);
        }
    }

    private void doRoute(WebRequest webRequest, WebResponse webResponse) throws IOException {
        Matcher matcher = Pattern.compile("/hangman/game/([a-f0-9]+)").matcher(webRequest.getPath());
        if (matcher.matches()) {
            webResponse.respond(SC_OK, hangmanController.findGame(matcher.group(1)));
            return;
        }

        if (webRequest.getPath().equals("/hangman/game")) {
            Optional<String> word = webRequest.getParameter("word");

            GameResponse gameResponse =
                    word.map(hangmanController::createNewGame)
                            .orElseGet(() -> hangmanController.createNewGame(null));

            webResponse.respond(SC_CREATED, gameResponse);
        }

        throw new NotFoundException(webRequest.getPath());
    }

}
