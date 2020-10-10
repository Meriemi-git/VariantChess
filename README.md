# VariantChess
Variant Chess is a chess game developed for Android offering to play the most famous rule variants online or locally.

* Project is developed in java 1.8
* Graphic part of application (board) is avalaible in 2D or 3D using libgdx
* Multiplayer feature are implemented by using Nakama framework.

@startuml

skinparam shadowing true
package fr.aboucorp.variantchess.app.db  #ffb3ba {
	abstract class fr.aboucorp.variantchess.app.db.VariantChessDatabase {
		+{static} databaseWriteExecutor: ExecutorService
		--
		+chessUserDao(): ChessUserDao
		+gameRulesDao(): GameRulesDao
		+{static} getDatabase(context: Context): VariantChessDatabase
	}

	package fr.aboucorp.variantchess.app.db.adapters {
		class fr.aboucorp.variantchess.app.db.adapters.GameRulesAdapter {
			--
			+GameRulesAdapter(context: Context, resource: int)
			..
			+getCount(): int
			+setGameRules(allGameRules: List<GameRules>)
			..
			+getDropDownView(position: int, convertView: View, parent: ViewGroup): View
			+getItem(position: int): GameRules
			+getView(position: int, convertView: View, viewGroup: ViewGroup): View
		}

	}

	package fr.aboucorp.variantchess.app.db.dao {
		interface fr.aboucorp.variantchess.app.db.dao.ChessUserDao {
			--
			+getAll(): List<ChessUser>
			+getConnected(): LiveData<ChessUser>
			..
			+delete(user: ChessUser)
			+disconnectAllOthers(username: String)
			+findByName(username: String): ChessUser
			+insertAll(users: ChessUser)
			+loadAllByIds(userIds: int[]): List<ChessUser>
			+update(user: ChessUser)
		}

		interface fr.aboucorp.variantchess.app.db.dao.GameRulesDao {
			--
			+getAll(): LiveData<List<GameRules>>
			..
			+delete(user: GameRules)
			+deleteAll()
			+findByName(name: String): GameRules
			+insertAll(gamerules: GameRules)
			+update(user: GameRules)
		}

	}

	package fr.aboucorp.variantchess.app.db.dto {
		class fr.aboucorp.variantchess.app.db.dto.ChessUserDto {
			--
			+{static} fromUserToChessUser(user: User): ChessUser
		}

	}

	package fr.aboucorp.variantchess.app.db.entities {
		class fr.aboucorp.variantchess.app.db.entities.ChessUser {
			+id: int
			+isConnected: boolean
			+metadata: String
			+userId: String
			+username: String
			--
		}

		class fr.aboucorp.variantchess.app.db.entities.GameRules {
			+balance: int
			+description: String
			+difficulty: int
			+icon: String
			+id: int
			+name: String
			--
		}

	}

	package fr.aboucorp.variantchess.app.db.repositories {
		class fr.aboucorp.variantchess.app.db.repositories.ChessUserRepository {
			--
			+ChessUserRepository(application: Application)
			..
			+getConnected(): LiveData<ChessUser>
			+setConnected(connecting: ChessUser)
			..
			+insert(chessUser: ChessUser)
			+update(chessUser: ChessUser)
		}

		class fr.aboucorp.variantchess.app.db.repositories.GameRulesRepository {
			--
			+GameRulesRepository(application: Application)
			..
			+getAllGameRules(): LiveData<List<GameRules>>
		}

	}

	package fr.aboucorp.variantchess.app.db.viewmodel {
		class fr.aboucorp.variantchess.app.db.viewmodel.GameRulesViewModel {
			--
			+GameRulesViewModel(application: Application)
			..
			+getAllGameRules(): LiveData<List<GameRules>>
		}

		class fr.aboucorp.variantchess.app.db.viewmodel.UserViewModel {
			--
			+UserViewModel(application: Application)
			..
			+getConnected(): LiveData<ChessUser>
			+setConnected(connected: ChessUser)
			..
			+disconnectUser()
			+insert(chessUser: ChessUser)
			#onCleared()
		}

	}

}

package fr.aboucorp.variantchess.app.exceptions {
	class fr.aboucorp.variantchess.app.exceptions.AuthentificationException {
		--
		+AuthentificationException(message: String)
	}

	class fr.aboucorp.variantchess.app.exceptions.HashException {
		--
		+HashException(message: String)
	}

	class fr.aboucorp.variantchess.app.exceptions.IllegalStateException {
		--
		+IllegalStateException(message: String)
	}

	class fr.aboucorp.variantchess.app.exceptions.IncorrectCredentials {
		--
		+IncorrectCredentials(message: String)
	}

	class fr.aboucorp.variantchess.app.exceptions.MailAlreadyRegistered {
		--
		+MailAlreadyRegistered(message: String)
	}

	class fr.aboucorp.variantchess.app.exceptions.UsernameAlreadyRegistered {
		--
		+UsernameAlreadyRegistered(message: String)
	}

}

package fr.aboucorp.variantchess.app.listeners {
	class fr.aboucorp.variantchess.app.listeners.GDXGestureListener {
		+{static} MAX_FOV: int
		+{static} MIN_FOV: int
		~previousTime: long
		--
		+GDXGestureListener(boardManager: BoardManager)
		..
		+fling(velocityX: float, velocityY: float, button: int): boolean
		+longPress(x: float, y: float): boolean
		+pan(x: float, y: float, deltaX: float, deltaY: float): boolean
		+panStop(x: float, y: float, pointer: int, button: int): boolean
		+pinch(initialPointer1: Vector2, initialPointer2: Vector2, pointer1: Vector2, pointer2: Vector2): boolean
		+pinchStop()
		+tap(x: float, y: float, count: int, button: int): boolean
		+touchDown(screenX: float, screenY: float, pointer: int, button: int): boolean
		+zoom(initialDistance: float, distance: float): boolean
	}

	class fr.aboucorp.variantchess.app.listeners.GDXInputAdapter {
		--
		+GDXInputAdapter(board3dManager: Board3dManager)
		..
		+keyDown(keycode: int): boolean
		+keyTyped(character: char): boolean
		+keyUp(keycode: int): boolean
		+mouseMoved(screenX: int, screenY: int): boolean
		+scrolled(amount: int): boolean
		+touchDown(screenX: int, screenY: int, pointer: int, button: int): boolean
		+touchDragged(screenX: int, screenY: int, pointer: int): boolean
		+touchUp(screenX: int, screenY: int, pointer: int, button: int): boolean
	}

	interface fr.aboucorp.variantchess.app.listeners.MatchEventListener {
		--
		+OnMatchEvent(event: GameEvent)
	}

	class fr.aboucorp.variantchess.app.listeners.TouchedModelFinder {
		--
		+TouchedModelFinder(boardManager: BoardManager)
		..
		+getTouched2DModel(screenX: float, screenY: float, models: GraphicGameArray): GraphicsGameElement
		+getTouched3DModel(screenX: float, screenY: float, models: GraphicGameArray): GraphicsGameElement
	}

}

package fr.aboucorp.variantchess.app.managers {
	class fr.aboucorp.variantchess.app.managers.MatchManager {
		#boardManager: BoardManager
		#chessMatch: ChessMatch
		#eventManager: GameEventManager
		#turnManager: TurnManager
		--
		+MatchManager(boardManager: BoardManager, gameEventManager: GameEventManager)
		..
		+getPartyInfos(): String
		..
		+OnBoardLoaded()
		+endTurn(event: MoveEvent)
		+receiveGameEvent(event: GameEvent)
		+startParty(chessMatch: ChessMatch)
		+stopParty()
	}

	class fr.aboucorp.variantchess.app.managers.OfflineMatchManager {
		--
		+OfflineMatchManager(boardManager: BoardManager, gameEventManager: GameEventManager)
	}

	class fr.aboucorp.variantchess.app.managers.OnlineMatchManager {
		--
		+OnlineMatchManager(boardManager: BoardManager, gameEventManager: GameEventManager, sessionManager: SessionManager, currentPlayer: ChessUser)
		..
		+onMatchData(matchData: MatchData)
		+playOppositeMove(boardState: String)
	}

	class fr.aboucorp.variantchess.app.managers.TurnManager {
		--
		+TurnManager(gameEventManager: GameEventManager)
		..
		+getCurrent(): Turn
		+getTurnColor(): ChessColor
		..
		+appendTurn(opposite: Turn)
		+endTurn(event: MoveEvent, fenFromBoard: String)
		+startParty(chessMatch: ChessMatch)
		+startTurn()
		+stopParty()
	}

	package fr.aboucorp.variantchess.app.managers.boards {
		abstract class fr.aboucorp.variantchess.app.managers.boards.BoardManager {
			#actualTurn: Turn
			#board: Board
			#board3dManager: Board3dManager
			#boardLoadingListener: BoardLoadingListener
			#boardStateBuilder: BoardStateBuilder
			#gameEventManager: GameEventManager
			#gameState: GameState
			#possiblesMoves: SquareList
			#previousTurn: Turn
			#ruleSet: AbstractRuleSet
			#selectedPiece: Piece
			--
			~BoardManager(board: Board, board3dManager: Board3dManager, ruleSet: AbstractRuleSet, gameEventManager: GameEventManager, boardStateBuilder: BoardStateBuilder)
			..
			+setBoardLoadingListener(boardLoadingListener: BoardLoadingListener)
			+getBoardState(): String
			+getCamera(): PerspectiveCamera
			+getFenFromBoard(): String
			+getGameState(): GameState
			+getModelsForTurn(): GraphicGameArray
			+getPossibleSquareModels(): GraphicGameArray
			+getWinner(): ChessColor
			..
			+IsTacticalViewOn(): boolean
			+getPieceFromLocation(location: Location): Piece
			+getSquareFromLocation(location: Location): Square
			+loadBoard(fenString: String): ChessColor
			#manageTurnEnd()
			#manageTurnStart(event: TurnStartEvent)
			#moveToSquare(to: Square): Piece
			+playTheOpposantMove(fenState: String): Turn
			+receiveGameEvent(event: GameEvent)
			+selectPiece(touched: Piece)
			+selectSquare(to: Square): Piece
			+startParty(chessMatch: ChessMatch)
			+stopParty()
			+stopWaitForNextTurn()
			+toogleTacticalView()
			+unHighlight()
			+waitForNextTurn()
		}

		interface fr.aboucorp.variantchess.app.managers.boards.BoardManager.BoardLoadingListener {
			--
			+OnBoardLoaded()
		}

		class fr.aboucorp.variantchess.app.managers.boards.ClassicBoardManager {
			--
			+ClassicBoardManager(board3dManager: Board3dManager, board: Board, ruleSet: ClassicRuleSet, gameEventManager: GameEventManager, classicFenBuilder: ClassicBoardStateBuilder)
			..
			+getPossibleSquareModels(): GraphicGameArray
			+getWinner(): ChessColor
		}

	}

}

package fr.aboucorp.variantchess.app.multiplayer #bae1ff {
	interface fr.aboucorp.variantchess.app.multiplayer.ChatListener {
		--
		+onChannelMessage(message: ChannelMessage)
		+onChannelPresence(presence: ChannelPresenceEvent)
	}

	interface fr.aboucorp.variantchess.app.multiplayer.MatchListener {
		--
		+onMatchData(matchData: MatchData)
	}

	interface fr.aboucorp.variantchess.app.multiplayer.MatchmakingListener {
		--
		+onMatchPresence(matchPresence: MatchPresenceEvent)
		+onMatchmakerMatched(matched: MatchmakerMatched)
	}

	class fr.aboucorp.variantchess.app.multiplayer.Metadata<T> {
		--
		+Metadata()
		+Metadata(values: Map<String,T>)
		..
		+getJsonFromMetadata(): String
		+setMetadataFromString(json: String)
	}

	class fr.aboucorp.variantchess.app.multiplayer.MetadataValue {
		--
		+MetadataValue(name: String, value: String)
		..
		+getName(): String
		+setName(name: String)
		+getValue(): String
		+setValue(value: String)
	}

	class fr.aboucorp.variantchess.app.multiplayer.NakamaSocketListener {
		--
		+NakamaSocketListener(sessionManager: SessionManager)
		..
		+setChatListener(chatListener: ChatListener)
		+setMatchListener(matchListener: MatchListener)
		+setMatchmakingListener(matchmakingListener: MatchmakingListener)
		..
		+onChannelMessage(message: ChannelMessage)
		+onChannelPresence(presence: ChannelPresenceEvent)
		+onDisconnect(t: Throwable)
		+onError(error: Error)
		+onMatchData(matchData: MatchData)
		+onMatchPresence(matchPresence: MatchPresenceEvent)
		+onMatchmakerMatched(matched: MatchmakerMatched)
		+onNotifications(notifications: NotificationList)
		+onStatusPresence(presence: StatusPresenceEvent)
		+onStreamData(data: StreamData)
		+onStreamPresence(presence: StreamPresenceEvent)
	}

	class fr.aboucorp.variantchess.app.multiplayer.SessionManager {
		+{static} SHARED_PREFERENCE_NAME: String
		--
		+setMatchListener(matchListener: MatchListener)
		+setMatchmakingListener(listener: MatchmakingListener)
		+getSession(): Session
		+getSocket(): SocketClient
		+setSocketClosed(socketClosed: boolean)
		..
		+cancelMatchMaking(ticket: String)
		+disconnect()
		+{static} getInstance(activity: Activity): SessionManager
		+getUsersFromMatched(matched: MatchmakerMatched): List<User>
		+joinMatchByToken(token: String): Match
		+launchMatchMaking(rulesName: String): String
		+mailExists(mail: String): boolean
		+sendData(data: Object, matchId: String, opcode: long)
		+signInWithEmail(mail: String, password: String): ChessUser
		+signUpWithEmail(mail: String, password: String, displayName: String): ChessUser
		+tryReconnectUser(): ChessUser
		+updateDisplayName(displayName: String): ChessUser
	}

}

package fr.aboucorp.variantchess.app.utils {
	class fr.aboucorp.variantchess.app.utils.ArgsKey {
		+{static} CHESS_MATCH: String
		+{static} CHESS_USER: String
		+{static} GAME_RULES: String
		+{static} IS_ONLINE: String
		--
	}

	abstract class fr.aboucorp.variantchess.app.utils.AsyncHandler {
		--
		#callbackOnUI()
		#error(ex: Exception)
		#executeAsync()
		+start()
	}

	enum fr.aboucorp.variantchess.app.utils.AuthType {
		GOOGLE
		MAIL
		MIXED
		--
	}

	class fr.aboucorp.variantchess.app.utils.Encryptor {
		--
		+{static} hash(plainText: String): String
	}

	class fr.aboucorp.variantchess.app.utils.ExceptionCauseCode {
		+{static} ABORTED: int
		+{static} ALREADY_EXISTS: int
		+{static} CANCELLED: int
		+{static} DATA_LOSS: int
		+{static} DEADLINE_EXCEEDED: int
		+{static} FAILED_PRECONDITION: int
		+{static} INTERNAL: int
		+{static} INVALID_ARGUMENT: int
		+{static} NOT_FOUND: int
		+{static} OK: int
		+{static} OUT_OF_RANGE: int
		+{static} PERMISSION_DENIED: int
		+{static} RESOURCE_EXHAUSTED: int
		+{static} UNAUTHENTICATED: int
		+{static} UNAVAILABLE: int
		+{static} UNIMPLEMENTED: int
		+{static} UNKNOWN: int
		--
		+{static} getCodeValueFromCause(t: Throwable): int
	}

	class fr.aboucorp.variantchess.app.utils.FragmentTag {
		+{static} AUTHENT: String
		+{static} BOARD: String
		+{static} GAME: String
		+{static} MATCH: String
		+{static} SETTINGS: String
		+{static} SIGNIN: String
		+{static} SIGNUP: String
		--
	}

	abstract class fr.aboucorp.variantchess.app.utils.GdxPostRunner {
		--
		#execute()
		+start()
		+startAsync()
	}

	class fr.aboucorp.variantchess.app.utils.JsonExtractor {
		--
		+{static} ectractAttributeByName(json: String, attrName: String): T
	}

	class fr.aboucorp.variantchess.app.utils.OPCode {
		+{static} SEND_NEW_FEN: long
		--
	}

	class fr.aboucorp.variantchess.app.utils.ResultType {
		+{static} LINK: int
		+{static} SIGNIN: int
		+{static} SIGNUP: int
		--
	}

	class fr.aboucorp.variantchess.app.utils.VariantVars {
		+{static} VARIANT_CHESS_TOKEN: String
		--
	}

	package fr.aboucorp.variantchess.app.utils.fen {
		abstract class fr.aboucorp.variantchess.app.utils.fen.BoardStateBuilder {
			#board: Board
			#ruleSet: AbstractRuleSet
			--
			+BoardStateBuilder(board: Board, abstractRuleSet: AbstractRuleSet)
			..
			+getFenFromBoard(actualTurn: Turn): String
			+getFrom(fenState: String): Location
			+getPiecePlayedFromState(fenState: String): PieceId
			+getStateFromBoard(actualTurn: Turn): String
			+getTo(fenState: String): Location
		}

		class fr.aboucorp.variantchess.app.utils.fen.ClassicBoardStateBuilder {
			--
			+ClassicBoardStateBuilder(board: ClassicBoard, classicRuleSet: ClassicRuleSet)
		}

	}

	package fr.aboucorp.variantchess.app.utils.validations {
		class fr.aboucorp.variantchess.app.utils.validations.MailExistsValidation {
			--
			+MailExistsValidation(sessionManager: SessionManager)
			..
			+compare(mail: String): boolean
		}

	}

}

package fr.aboucorp.variantchess.app.views.activities {
	class fr.aboucorp.variantchess.app.views.activities.BoardActivity {
		+board_panel: FrameLayout
		--
		+getViewModelStore(): ViewModelStore
		..
		+exit()
		+onBackPressed()
		+onConfigurationChanged(config: Configuration)
		#onCreate(savedInstanceState: Bundle)
		+onSaveInstanceState(savedInstanceState: Bundle)
		+receiveGameEvent(event: GameEvent)
	}

	class fr.aboucorp.variantchess.app.views.activities.MainActivity {
		--
		#onCreate(savedInstanceState: Bundle)
		+onCreateOptionsMenu(menu: Menu): boolean
		+onOptionsItemSelected(item: MenuItem): boolean
		+onPrepareOptionsMenu(menu: Menu): boolean
		+userIsConnected(connected: ChessUser)
	}

}

package fr.aboucorp.variantchess.app.views.fragments {
	class fr.aboucorp.variantchess.app.views.fragments.AuthentFragment {
		--
		+onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View
		+onViewCreated(view: View, savedInstanceState: Bundle)
	}

	class fr.aboucorp.variantchess.app.views.fragments.GameRulesFragment {
		--
		+onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View
		+onItemSelected(adapterView: AdapterView<?>, view: View, position: int, id: long)
		+onNothingSelected(adapterView: AdapterView<?>)
		+onViewCreated(view: View, savedInstanceState: Bundle)
	}

	class fr.aboucorp.variantchess.app.views.fragments.MatchmakingFragment {
		--
		+setArguments(args: Bundle)
		..
		+onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View
		+onMatchPresence(matchPresence: MatchPresenceEvent)
		+onMatchmakerMatched(matched: MatchmakerMatched)
		+onViewCreated(view: View, savedInstanceState: Bundle)
	}

	class fr.aboucorp.variantchess.app.views.fragments.SettingsFragment {
		+{static} IS_TACTICAL_MODE_ON: String
		--
		+SettingsFragment()
		..
		+onCreatePreferences(savedInstanceState: Bundle, rootKey: String)
	}

	class fr.aboucorp.variantchess.app.views.fragments.SignInFragment {
		--
		+SignInFragment()
		..
		+onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View
		+onViewCreated(view: View, savedInstanceState: Bundle)
	}

	class fr.aboucorp.variantchess.app.views.fragments.SignUpFragment {
		--
		+onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View
		+onViewCreated(view: View, savedInstanceState: Bundle)
	}

	abstract class fr.aboucorp.variantchess.app.views.fragments.VariantChessFragment {
		--
		#bindListeners()
		#bindViews()
	}

}

fr.aboucorp.variantchess.app.db.adapters.GameRulesAdapter --o "*" fr.aboucorp.variantchess.app.db.entities.GameRules
fr.aboucorp.variantchess.app.db.repositories.ChessUserRepository --* fr.aboucorp.variantchess.app.db.dao.ChessUserDao
fr.aboucorp.variantchess.app.db.repositories.ChessUserRepository --* fr.aboucorp.variantchess.app.db.entities.ChessUser
fr.aboucorp.variantchess.app.db.repositories.GameRulesRepository --* fr.aboucorp.variantchess.app.db.dao.GameRulesDao
fr.aboucorp.variantchess.app.db.viewmodel.GameRulesViewModel --* fr.aboucorp.variantchess.app.db.repositories.GameRulesRepository
fr.aboucorp.variantchess.app.db.viewmodel.UserViewModel --* fr.aboucorp.variantchess.app.db.repositories.ChessUserRepository
fr.aboucorp.variantchess.app.exceptions.IncorrectCredentials --|> fr.aboucorp.variantchess.app.exceptions.AuthentificationException
fr.aboucorp.variantchess.app.exceptions.MailAlreadyRegistered --|> fr.aboucorp.variantchess.app.exceptions.AuthentificationException
fr.aboucorp.variantchess.app.exceptions.UsernameAlreadyRegistered --|> fr.aboucorp.variantchess.app.exceptions.AuthentificationException
fr.aboucorp.variantchess.app.listeners.GDXGestureListener --* fr.aboucorp.variantchess.app.managers.boards.BoardManager
fr.aboucorp.variantchess.app.listeners.GDXGestureListener --* fr.aboucorp.variantchess.app.listeners.TouchedModelFinder
fr.aboucorp.variantchess.app.listeners.TouchedModelFinder --* fr.aboucorp.variantchess.app.managers.boards.BoardManager
fr.aboucorp.variantchess.app.managers.MatchManager ..|> fr.aboucorp.variantchess.app.managers.boards.BoardManager.BoardLoadingListener
fr.aboucorp.variantchess.app.managers.MatchManager --* fr.aboucorp.variantchess.app.managers.boards.BoardManager : boardManager
fr.aboucorp.variantchess.app.managers.MatchManager --* fr.aboucorp.variantchess.app.managers.TurnManager : turnManager
fr.aboucorp.variantchess.app.managers.OfflineMatchManager --|> fr.aboucorp.variantchess.app.managers.MatchManager
fr.aboucorp.variantchess.app.managers.OnlineMatchManager --|> fr.aboucorp.variantchess.app.managers.MatchManager
fr.aboucorp.variantchess.app.managers.OnlineMatchManager ..|> fr.aboucorp.variantchess.app.multiplayer.MatchListener
fr.aboucorp.variantchess.app.managers.OnlineMatchManager --* fr.aboucorp.variantchess.app.multiplayer.SessionManager
fr.aboucorp.variantchess.app.managers.OnlineMatchManager --* fr.aboucorp.variantchess.app.db.entities.ChessUser
fr.aboucorp.variantchess.app.managers.boards.BoardManager --* fr.aboucorp.variantchess.app.managers.boards.BoardManager.BoardLoadingListener : boardLoadingListener
fr.aboucorp.variantchess.app.managers.boards.BoardManager --* fr.aboucorp.variantchess.app.utils.fen.BoardStateBuilder : boardStateBuilder
fr.aboucorp.variantchess.app.managers.boards.ClassicBoardManager --|> fr.aboucorp.variantchess.app.managers.boards.BoardManager
fr.aboucorp.variantchess.app.multiplayer.NakamaSocketListener *--* fr.aboucorp.variantchess.app.multiplayer.SessionManager
fr.aboucorp.variantchess.app.multiplayer.NakamaSocketListener --* fr.aboucorp.variantchess.app.multiplayer.MatchmakingListener
fr.aboucorp.variantchess.app.multiplayer.NakamaSocketListener --* fr.aboucorp.variantchess.app.multiplayer.ChatListener
fr.aboucorp.variantchess.app.multiplayer.NakamaSocketListener --* fr.aboucorp.variantchess.app.multiplayer.MatchListener
fr.aboucorp.variantchess.app.utils.fen.ClassicBoardStateBuilder --|> fr.aboucorp.variantchess.app.utils.fen.BoardStateBuilder
fr.aboucorp.variantchess.app.utils.validations.MailExistsValidation --* fr.aboucorp.variantchess.app.multiplayer.SessionManager
fr.aboucorp.variantchess.app.views.activities.BoardActivity --* fr.aboucorp.variantchess.app.managers.boards.ClassicBoardManager
fr.aboucorp.variantchess.app.views.activities.BoardActivity --* fr.aboucorp.variantchess.app.managers.MatchManager
fr.aboucorp.variantchess.app.views.activities.BoardActivity --* fr.aboucorp.variantchess.app.db.entities.ChessUser
fr.aboucorp.variantchess.app.views.activities.BoardActivity --* fr.aboucorp.variantchess.app.db.entities.GameRules
fr.aboucorp.variantchess.app.views.activities.BoardActivity --* fr.aboucorp.variantchess.app.db.viewmodel.UserViewModel
fr.aboucorp.variantchess.app.views.activities.MainActivity --* fr.aboucorp.variantchess.app.multiplayer.SessionManager
fr.aboucorp.variantchess.app.views.activities.MainActivity --* fr.aboucorp.variantchess.app.db.viewmodel.UserViewModel
fr.aboucorp.variantchess.app.views.fragments.AuthentFragment --|> fr.aboucorp.variantchess.app.views.fragments.VariantChessFragment
fr.aboucorp.variantchess.app.views.fragments.GameRulesFragment --|> fr.aboucorp.variantchess.app.views.fragments.VariantChessFragment
fr.aboucorp.variantchess.app.views.fragments.GameRulesFragment --* fr.aboucorp.variantchess.app.db.viewmodel.GameRulesViewModel
fr.aboucorp.variantchess.app.views.fragments.GameRulesFragment --o "*" fr.aboucorp.variantchess.app.db.entities.GameRules
fr.aboucorp.variantchess.app.views.fragments.MatchmakingFragment --|> fr.aboucorp.variantchess.app.views.fragments.VariantChessFragment
fr.aboucorp.variantchess.app.views.fragments.MatchmakingFragment ..|> fr.aboucorp.variantchess.app.multiplayer.MatchmakingListener
fr.aboucorp.variantchess.app.views.fragments.MatchmakingFragment --* fr.aboucorp.variantchess.app.multiplayer.SessionManager
fr.aboucorp.variantchess.app.views.fragments.MatchmakingFragment --* fr.aboucorp.variantchess.app.db.entities.GameRules
fr.aboucorp.variantchess.app.views.fragments.MatchmakingFragment --* fr.aboucorp.variantchess.app.db.entities.ChessUser
fr.aboucorp.variantchess.app.views.fragments.MatchmakingFragment --* fr.aboucorp.variantchess.app.db.viewmodel.UserViewModel
fr.aboucorp.variantchess.app.views.fragments.SettingsFragment --* fr.aboucorp.variantchess.app.multiplayer.SessionManager
fr.aboucorp.variantchess.app.views.fragments.SignInFragment --|> fr.aboucorp.variantchess.app.views.fragments.VariantChessFragment
fr.aboucorp.variantchess.app.views.fragments.SignInFragment --* fr.aboucorp.variantchess.app.multiplayer.SessionManager
fr.aboucorp.variantchess.app.views.fragments.SignInFragment --* fr.aboucorp.variantchess.app.db.repositories.ChessUserRepository
fr.aboucorp.variantchess.app.views.fragments.SignUpFragment --|> fr.aboucorp.variantchess.app.views.fragments.VariantChessFragment
fr.aboucorp.variantchess.app.views.fragments.SignUpFragment --* fr.aboucorp.variantchess.app.multiplayer.SessionManager

@enduml


