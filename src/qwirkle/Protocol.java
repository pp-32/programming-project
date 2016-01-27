package qwirkle;
/**
 * Eindopdracht Module 2 2015-2016: 'Qwirkle'.
 * Interface Protocol met daarin de gemaakte afspraken tijdens werkcollege 06-01-2016
 * @author  Wim Kamerman
 * @version 1.0.1 (13-01-2016)
 *
 * Changelog:
 *
 * v1.0.1
 * - Meerdere kleine fouten in beschrijvingen van parameters verbeterd.
 * - Bij een paar ingewikkelde commando's voorbeelden toegevoegd.
 * - Toegevoegde eis aan commandostructuur met pipes: spatie voor en achter de pipe is verplicht. 
 *   Zo kun je op meerdere manieren het commando uitlezen.
 *
 * v1.0.0
 * - Opzet Protocol
 * - Y-coordinaten bord omgedraaid, niet volgens afspraak in werkcollege!
 * - Wijzigen Challenge server respons, van CHALLENGE_RESPONSE naar CHALLENGE_RESULT, 
     volgens afspraak in werkcollege!
 *
 */


public interface Protocol {

	/**
	 * LET OP: VOLGORDE VAN PARAMETERS BIJ FUNCTIES IS EXPLICIET. 
	 * VOLG VOLGORDE ZOALS IN JAVADOC AANGEGEVEN.
	 * LET OP: ALLE PARAMETERS ZIJN VAN HET TYPE "STRING" EN GESCHEIDEN DOOR EEN SPATIE
	 * LET OP: PARAMETERS MOGEN GEEN SPATIES EN PIPES (|) BEVATTEN!
	 * LET OP: BIJ HET VERSTUREN VAN COMMANDO'S MET DOOR PIPES GESCHEIDEN 'BLOKKEN PARAMETERS',
	 * EEN SPATIE VOOR EN ACHTER DE PIPE INVOEGEN.
	 *
	 * In dit document worden de coordinaten van blokken gemarkeerd als 2 aparte strings, 
	 * eerst "X", dan "Y".
	 * Opzet coordinaten bord, gescreven als (X,Y):
	 *
	 * 	(-1,-1)	(0,-1)	(1,-1)
	 *		|	|		|
	 *	(-1,0) 	(0,0)	(1,0)
	 *		|	|		|
	 *	(-1,1) 	(0,1)	(1,1)
	 *
	 *	Ofwel:
	 *			^
	 *			|
	 *			Y-
	 *	<- X-		X+ ->
	 *			Y+
	 *  		|
	 *  		v
	 *
	 */

	/**
	 * Definitie van de mogelijke kleuren van blokken en bijbehorende nummers.
	 */
	int RED = 1;
	int ORANGE = 2;
	int YELLOW = 3;
	int GREEN = 4;
	int BLUE = 5;
	int PURPLE = 6;

	/**
	 * Definitie van de mogelijke types van blokken en bijbehorende nummers.
	 */

	int CIRCLE = 1;
	int CROSS = 2;
	int DIAMOND = 3;
	int RECTANGLE = 4;
	int STAR = 5;
	int CLUBS = 6;

	/**
	 * Wordt gebruikt om de client in te loggen op de server.
	 *
	 * Lijst van argumenten:
	 * - name = Naam van deze client
	 * - chat = Ondersteunt deze client chatfunctionaliteit?
	 * - challenge = Ondersteunt deze client challengefunctionaliteit?
	 * - leaderboard = Ondersteunt deze client leaderboardfunctionaliteit?
	 * - security = Ondersteunt deze client securityfunctionaliteit?
	 *
	 * Eisen argumenten:
	 * - name bevat geen spaties
	 * - chat = 1 || 0
	 * - challenge = 1 || 0
	 * - leaderboard = 1 || 0
	 * - security = 1 || 0
	 *
	 * Voorbeeld: pietje wil joinen, client ondersteunt wel chat, geen challenge,
	 * geen leaderboard en geen security:
	 * Code: "joinrequest pietje 1 0 0 0"
	 *
	 * Richting: Client -> Server
	 */
	String CLIENT_JOINREQUEST = "joinrequest";

	/**
	 * Wordt gebruikt om client te accepteren en vanuit de server de client te melden 
	 * welke functionaliteit ondersteund wordt.
	 *
	 * Lijst van argumenten:
	 * - name = Naam van geaccepteerde client
	 * - chat = Ondersteunt deze server chat-functionaliteit?
	 * - challenge = Ondersteunt deze server challenge-functionaliteit?
	 * - leaderboard = Ondersteunt deze server leaderboardfunctionaliteit?
	 * - security = Ondersteunt deze server securityfunctionaliteit?
	 *
	 * Eisen argumenten:
	 * - name bevat geen spaties
	 * - chat = 1 || 0
	 * - challenge = 1 || 0
	 * - leaderboard = 1 || 0
	 * - security = 1 || 0
	 *
	 * Voorbeeld: pietje accepteren, server ondersteunt geen chat, geen challenge, 
	 * geen leaderboard en geen security:
	 * Code: "acceptrequest pietje 0 0 0 0"
	 *
	 * Richting: Server -> Client
	 */
	String SERVER_ACCEPTREQUEST = "acceptrequest";

	/**
	 * Wordt gebruikt om aan de server duidelijk te maken dat men deel wil nemen aan een spel.
	 *
	 * Argument:
	 * - amount = Aantal spelers in spel waaraan deelgenomen wordt. Als geen amount 
	 *            meegegeven wordt, maakt het niet uit hoeveel spelers.
	 *
	 * Eis aan argument:
	 * 2 =< amount <= 4 || argument == null
	 *
	 * Richting: Client -> Server
	 */
	String CLIENT_GAMEREQUEST = "gamerequest";

	/**
	 * Wordt gebruikt om aan clients duidelijk te maken dat een spel gestart wordt.
	 *
	 * Argument:
	 * - names = Lijst met namen van deelnemers, gesorteerd op beurtvolgorde, 
	 *           gescheiden door spaties.
	 *
	 * Eis aan argument:
	 * - names: minimaal 2 namen
	 *
	 * Voorbeeld: start een spel met 3 personen: Tim, Rik en Wessel:
	 * Code: "startgame Tim Rik Wessel"
	 *
	 * Richting: Server -> Client
	 */
	String SERVER_STARTGAME = "startgame";

	/**
	 * Wordt gebruikt om een client een set stenen te geven.
	 *
	 * Lijst van argumenten:
	 * - amount = Aantal stenen dat gegeven wordt
	 * Per steen de volgende 2 waardes invoeren, per steen gescheiden door een pipe (|)
	 * - type = Type van steen
	 * - color = Kleur van steen
	 *
	 * Eisen aan argumenten:
	 * - 1 =< amount <= 6
	 * - type = CIRCLE || CROSS || DIAMOND || RECTANGLE || STAR || CLUBS
	 * - color = RED || ORANGE || YELLOW || GREEN || BLUE || PURPLE
	 *
	 * Voorbeeld: geef client 3 stenen, een gele cirkel, een groene diamant en een blauwe ster:
	 * Code: "givestones 3 1 3 | 3 4 | 5 5"
	 *
	 * Richting: Server -> Client
	 */
	String SERVER_GIVESTONES = "givestones";

	/**
	 * Wordt gebruikt om client te vertellen dat ze een zet moeten doen.
	 * Richting: Server -> Client
	 */
	String SERVER_MOVEREQUEST = "moverequest";

	/**
	 * Wordt gebruikt om server mede te delen welke zet gedaan wordt.
	 * Lijst argumenten:
	 * - amount = Aantal stenen dat gezet wordt
	 * Per steen de volgende 4 waardes invoeren, per steen gescheiden door een pipe (|)
	 * - type = Type van steen
	 * - color = Kleur van steen
	 * - locationX = X-coordinaat van steen
	 * - locationY = X-coordinaat van steen
	 *
	 * Eisen aan argumenten:
	 * - 1 =< amount <= 6
	 * - type = CIRCLE || CROSS || DIAMOND || RECTANGLE || STAR || CLUBS
	 * - color = RED || ORANGE || YELLOW || GREEN || BLUE || PURPLE
	 * - locationX = integer
	 * - locationY = integer
	 *
	 * Voorbeeld: zet 2 stenen, een blauw vierkant op (3,4) en een rood vierkant op (3,5):
	 * Code: "setmove 2 4 5 3 4 | 4 1 3 5"
	 *
	 * Richting: Client -> Server
	 */
	String CLIENT_SETMOVE = "setmove";

	/**
	 * Wordt gebruikt om alle clients mede te delen welke zet gedaan is. 
	 * Is kopie van CLIENT_SETMOVE, plus naam speler en aantal punten
	 * 
	 * Lijst argumenten:
	 * - name = Naam van speler die zet heeft gedaan
	 * - score = Score behaald door zet van client
	 * - amount = Aantal stenen dat gezet wordt
	 * Per steen de volgende 4 waardes invoeren, per steen gescheiden door een pipe (|)
	 * - type = Type van steen
	 * - color = Kleur van steen
	 * - locationX = X-coordinaat van steen
	 * - locationY = X-coordinaat van steen
	 *
	 * Eisen aan argumenten:
	 * - name bevat geen spaties
	 * - score >= 0
	 * - 1 =< amount <= 6
	 * - type = CIRCLE || CROSS || DIAMOND || RECTANGLE || STAR || CLUBS
	 * - color = RED || ORANGE || YELLOW || GREEN || BLUE || PURPLE
	 * - locationX = integer
	 * - locationY = integer
	 *
	 * Voorbeeld: client 'Ruud' zet 2 stenen, een blauw vierkant op (3,4) en een rood 
	 *            vierkant op (3,5), en scoort daarmee 3 punten:
	 * Code: "notifymove Ruud 3 2 4 5 3 4 | 4 1 3 5"
	 *
	 * Richting: Server -> Client
	 */
	String SERVER_NOTIFYMOVE = "notifymove";

	/**
	 * Wordt gebruikt om een set stenen met de server te ruilen.
	 * 
	 * Lijst argumenten:
	 * - amount = Aantal stenen dat geruild moet worden
	 * Per steen de volgende 2 waardes invoeren, per steen gescheiden door een pipe (|)
	 * - type = Type van steen
	 * - color = Kleur van steen
	 *
	 * Eisen aan argumenten:
	 * - 1 =< amount <= 6
	 * - type = CIRCLE || CROSS || DIAMOND || RECTANGLE || STAR || CLUBS
	 * - color = RED || ORANGE || YELLOW || GREEN || BLUE || PURPLE
	 *
	 * Richting: Client -> Server
	 */
	String CLIENT_DOTRADE = "givestones";

	/**
	 * Wordt gebruikt om alle clients mede te delen welke speler welk aantal stenen heeft geruild.
	 * 
	 * Lijst argumenten:
	 * - name = Naam van speler die stenen heeft geruild
	 * - amount = Aantal stenen dat geruild is
	 *
	 * Eisen aan argumenten:
	 * - name bevat geen spaties
	 * - 1 =< amount <= 6
	 *
	 * Voorbeeld: client 'Teun' heeft 4 stenen geruild:
	 * Code: "notifytrade Teun 4"
	 *
	 * Richting: Server -> Client
	 */
	String SERVER_NOTIFYTRADE = "notifytrade";

	/**
	 * Wordt gebruikt om alle clients mede te delen welke speler heeft gewonnen, 
	 * en met welke score. Daarnaast scores van alle spelers. 
	 * 
	 * Per speler de volgende 2 waardes invoeren, per speler gescheiden door 
	 * een pipe (|), aflopend op score. (winnaar eerst).
	 * 
	 * Lijst argumenten:
	 * - name = naam van speler
	 * - score = score van speler
	 *
	 * Eisen aan argumenten:
	 * - name bevat geen spaties
	 * - score >= 0
	 *
	 * Voorbeeld: spel met 3 spelers, 'Wessel' heeft met 67 punten gewonnen
	 *            van 'Teun' met 45 punten en 'Rik' met 54 punten:
	 * Code: "gameover Wessel 67 | Teun 45 | Rik 54"
	 *
	 * Richting: Server -> Client
	 */
	String SERVER_GAMEOVER = "gameover";

	/**
	 * Wordt gebruikt om informatie te verschaffen over een fout die zich heeft voorgedaan.
	 *
	 * Argument:
	 * - reason = Reden foutmelding
	 *
	 * Voorbeeld: client stuurt ongeldige zet:
	 * Code: "invalidcommand Ongeldige zet geplaatst"
	 *
	 * Richting: Server -> Client
	 */
	String SERVER_INVALIDCOMMAND = "invalidcommand";

	/**
	 * Wordt gebruikt door server om spelers te laten weten welke client is weggevallen.
	 *
	 * Argument:
	 * - name = naam van weggevallen speler
	 *
	 * Richting: Server -> Client
	 */
	String SERVER_CONNECTIONLOST = "connectionlost";


	/* ---------------------------EXTRA Functionaliteit ---------------------------	 */

	/**
	 * Wordt gebruikt om een chatbericht te verzenden van client naar server
	 *
	 * Argument:
	 * - message = String met chatbericht.
	 *
	 * Richting: Client -> Server
	 */
	String CLIENT_CHAT = "client_chat";

	/**
	 * Wordt gebruikt om een chatbericht te verzenden van server naar alle clients
	 *
	 * Argumenten:
	 * - name = String met afzender.
	 * - message = String met chatbericht.
	 *
	 * Richting: Server -> Client
	 */
	String SERVER_CHAT = "server_chat";

	/**
	 * Wordt gebruikt om een lijst van challegable clients aan client te geven.
	 *
	 * Argument:
	 * - name = Lijst van challengable clients, gescheiden door pipes (|)
	 *
	 * Eis aan argument:
	 * - \foreach name bevat geen spatie of pipe
	 *
	 * Voorbeeld: de clients "Anna", "Iris" en "Liesbeth" zijn beschikbaar voor een challenge:
	 * Code: "challenge_list Anna | Iris | Liesbeth"
	 *
	 * Richting: Server -> Client
	 */
	String SERVER_CHALLENGE_LIST = "challenge_list";

	/**
	 * Wordt gebruikt om de server te informeren over een gevraagde challenge.
	 *
	 * Argument:
	 * - name = Lijst met namen van andere clients die gechallenged moeten worden, 
	 * 			gescheiden door pipes (|).
	 *
	 * Eisen aan argument:
	 * - \foreach name bevat geen spatie of pipe
	 * - (1 <= aantal namen <= 3)
	 *
	 * Richting: Client -> Server
	 */
	String CLIENT_DO_CHALLENGE = "do_challenge";

	/**
	 * Wordt gebruikt om een client te informeren van een challenge.
	 *
	 * Argument:
	 * - name = Lijst met namen van andere clients die gechallenged moeten worden, 
	 *          gescheiden door pipes (|), waarbij de uitdager vooraan staat.
	 *
	 * Eisen aan argument:
	 * - \foreach name bevat geen spatie of pipe
	 * - (1 <= aantal namen <= 3)
	 *
	 * Richting: Server -> Client
	 */
	String SERVER_NOTIFY_CHALLENGE = "notify_challenge";

	/**
	 * Wordt gebruikt door een client om een challenge al dan niet te accepteren.
	 *
	 * Argument:
	 * - response = 	0 als client niet mee wenst te doen aan challenge
	 * 						1 als client wel mee wil doen aan challenge
	 *
	 * Eis aan argument:
	 * - response = 0 || 1
	 *
	 * Richting: Client -> Server
	 */
	String CLIENT_CHALLENGE_RESPONSE = "challenge_response";

	/**
	 * Wordt gebruikt om clients te informeren van het al dan niet doorgaan van de challenge
	 *
	 * Argumenten:
	 * - response = 	0 als er een of meerdere clients de challenge geweigerd hebben
	 * 										1 als alle clients de challenge geaccepteerd hebben.
	 * - name = naam van challenger
	 *
	 * Eisen aan argumenten:
	 * - response = 0 || 1
	 * - name zonder spatie
	 *
	 * Richting: Server -> Client
	 */
	String SERVER_CHALLENGE_RESULT = "challenge_result";

	/**
	 * Wordt gebruikt door client om leaderboard op te vragen.
	 * Richting: Client -> Server
	 */
	String CLIENT_GET_LEADERBOARD = "get_leaderboard";

	/**
	 * Wordt gebruikt om alle leaderboard naar client te sturen.
	 * 
	 * Argumenten:
	 * Per speler de volgende 3 waardes invoeren, per speler gescheiden door een pipe (|)
	 * - name = Naam van speler die zet heeft gedaan
	 * - score = Score behaald door zet van client
	 * - time = tijdstip behalen score uitgedrukt in aantal seconden 
	 * 			sinds 1 januari 1970 (System.currentTimeMillis())
	 *
	 * Eisen aan argumenten:
	 * - name bevat geen spaties
	 * - score >= 0
	 * - time = integer
	 *
	 * Richting: Server -> Client
	 */
	String SERVER_SEND_LEADERBOARD = "send_leaderboard";

}
