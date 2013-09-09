package com.dylan.orzeye;

import java.lang.reflect.Field;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class NotesFragment extends Fragment implements ListView.OnScrollListener, OnItemClickListener,
		android.view.View.OnClickListener {
	private Handler handler;
	private DisapearThread disapearThread;
	/** 标识List的滚动状态。 */
	private int scrollState;
	private ListAdapter listAdapter;
	private ListView listMain;
	private TextView txtOverlay;
	private WindowManager windowManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
    		Bundle savedInstanceState) {
    	View view = inflater.inflate( R.layout.activity_notes,container, false);
    	handler = new Handler();
   		// 初始化首字母悬浮提示框
   		txtOverlay = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.popup_char_hint, null);
   		txtOverlay.setVisibility(View.INVISIBLE);
   		WindowManager.LayoutParams lp = new WindowManager.LayoutParams(LayoutParams.WRAP_CONTENT,
   				LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_APPLICATION,
   				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
   						| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, PixelFormat.TRANSLUCENT);
   		windowManager = (WindowManager)getActivity().getSystemService(Context.WINDOW_SERVICE);
   		windowManager.addView(txtOverlay, lp);
   		// 初始化ListAdapter
   		listAdapter = new ListAdapter(getActivity(), stringArr, this);
   		listMain = (ListView) view.findViewById(R.id.listInfo);
   		listMain.setOnItemClickListener(this);
   		listMain.setOnScrollListener(this);
   		listMain.setAdapter(listAdapter);
   		changeFastScrollerDrawable(listMain);
   		disapearThread = new DisapearThread();
    	   return view;
    }

	/** 更改指定ListView的快速滚动条图标。 */
	private void changeFastScrollerDrawable(ListView list) {
		try {
			Field f = AbsListView.class.getDeclaredField("mFastScroller");
			f.setAccessible(true);
			Object obj = f.get(list);
			f = f.getType().getDeclaredField("mThumbDrawable");
			f.setAccessible(true);
			Drawable drawable = (Drawable) f.get(obj);
			drawable = getResources().getDrawable(R.drawable.fast_scroller_img);
			f.set(obj, drawable);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/** ListView.OnScrollListener */
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
			int totalItemCount) {
		// 以中间的ListItem为标准项。
		txtOverlay.setText(String.valueOf(stringArr[firstVisibleItem + (visibleItemCount >> 1)]
				.charAt(0)));
	}

	/** ListView.OnScrollListener */
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		this.scrollState = scrollState;
		if (scrollState == ListView.OnScrollListener.SCROLL_STATE_IDLE) {
			handler.removeCallbacks(disapearThread);
			// 提示延迟1.5s再消失
			boolean bool = handler.postDelayed(disapearThread, 1500);
			Log.d("ANDROID_INFO", "postDelayed=" + bool);
		} else {
			txtOverlay.setVisibility(View.VISIBLE);
		}
	}

	/** OnItemClickListener */
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		
	}

	/**
	 * View.OnClickListener <br/>
	 * 点击“咧牙”图片。<br/>
	 */
	public void onClick(View view) {
		if (view instanceof ImageView) {
			// "咧牙"图标
			int position = ((Integer) view.getTag()).intValue();
			ActionItem actionAdd = new ActionItem(getResources().getDrawable(R.drawable.icon_info),
					"Info", this);
			ActionItem actionWeb = new ActionItem(getResources().getDrawable(R.drawable.icon_web),
					"Web", this);
			ActionItem actionEMail = new ActionItem(getResources().getDrawable(
					R.drawable.icon_email), "Email", this);
			QuickActionBar qaBar = new QuickActionBar(view, position);
			qaBar.setEnableActionsLayoutAnim(true);
			qaBar.addActionItem(actionAdd);
			qaBar.addActionItem(actionWeb);
			qaBar.addActionItem(actionEMail);
			qaBar.show();
		} else if (view instanceof LinearLayout) {
			// ActionItem组件
			LinearLayout actionsLayout = (LinearLayout) view;
			QuickActionBar bar = (QuickActionBar) actionsLayout.getTag();
			bar.dismissQuickActionBar();
			int listItemIdx = bar.getListItemIndex();
			TextView txtView = (TextView) actionsLayout.findViewById(R.id.qa_actionItem_name);
			String actionName = txtView.getText().toString();
			String personalName = stringArr[listItemIdx];
			String url = ListAdapter.URL_PREFIX + personalName.replace(" ", "%20");
			if (actionName.equals("Info")) {
				//showInfo(personalName, url);
			} else if (actionName.equals("Web")) {
				//go2Web(url);
			} else if (actionName.equals("Email")) {
				///sendEMail(personalName, url);
			}
		}
	}

	private class DisapearThread implements Runnable {
		public void run() {
			// 避免在1.5s内，用户再次拖动时提示框又执行隐藏命令。
			if (scrollState == ListView.OnScrollListener.SCROLL_STATE_IDLE) {
				txtOverlay.setVisibility(View.INVISIBLE);
			}
		}
	}

	public void onDestroy() {
		super.onDestroy();
		// 将txtOverlay删除。
		txtOverlay.setVisibility(View.INVISIBLE);
		windowManager.removeView(txtOverlay);
	}

	private String[] stringArr = { "Abbaye de Belloc", "Abbaye du Mont des Cats", "Abertam",
			"Abondance", "Ackawi", "Acorn", "Adelost", "Affidelice au Chablis", "Afuega'l Pitu",
			"Airag", "Airedale", "Aisy Cendre", "Allgauer Emmentaler", "Alverca", "Ambert",
			"American Cheese", "Ami du Chambertin", "Anejo Enchilado", "Anneau du Vic-Bilh",
			"Anthoriro", "Appenzell", "Aragon", "Ardi Gasna", "Ardrahan", "Armenian String",
			"Aromes au Gene de Marc", "Asadero", "Asiago", "Aubisque Pyrenees", "Autun",
			"Avaxtskyr", "Baby Swiss", "Babybel", "Baguette Laonnaise", "Bakers", "Baladi",
			"Balaton", "Bandal", "Banon", "Barry's Bay Cheddar", "Basing", "Basket Cheese",
			"Bath Cheese", "Bavarian Bergkase", "Baylough", "Beaufort", "Beauvoorde",
			"Beenleigh Blue", "Beer Cheese", "Bel Paese", "Bergader", "Bergere Bleue", "Berkswell",
			"Beyaz Peynir", "Bierkase", "Bishop Kennedy", "Blarney", "Bleu d'Auvergne",
			"Bleu de Gex", "Bleu de Laqueuille", "Bleu de Septmoncel", "Bleu Des Causses", "Blue",
			"Blue Castello", "Blue Rathgore", "Blue Vein (Australian)", "Blue Vein Cheeses",
			"Bocconcini", "Bocconcini (Australian)", "Boeren Leidenkaas", "Bonchester", "Bosworth",
			"Bougon", "Boule Du Roves", "Boulette d'Avesnes", "Boursault", "Boursin", "Bouyssou",
			"Bra", "Braudostur", "Breakfast Cheese", "Brebis du Lavort", "Brebis du Lochois",
			"Brebis du Puyfaucon", "Bresse Bleu", "Brick", "Brie", "Brie de Meaux",
			"Brie de Melun", "Brillat-Savarin", "Brin", "Brin d' Amour", "Brin d'Amour",
			"Brinza (Burduf Brinza)", "Briquette de Brebis", "Briquette du Forez", "Broccio",
			"Broccio Demi-Affine", "Brousse du Rove", "Bruder Basil",
			"Brusselae Kaas (Fromage de Bruxelles)", "Bryndza", "Buchette d'Anjou", "Buffalo",
			"Burgos", "Butte", "Butterkase", "Button (Innes)", "Buxton Blue", "Cabecou", "Caboc",
			"Cabrales", "Cachaille", "Caciocavallo", "Caciotta", "Caerphilly", "Cairnsmore",
			"Calenzana", "Cambazola", "Camembert de Normandie", "Canadian Cheddar", "Canestrato",
			"Cantal", "Caprice des Dieux", "Capricorn Goat", "Capriole Banon", "Carre de l'Est",
			"Casciotta di Urbino", "Cashel Blue", "Castellano", "Castelleno", "Castelmagno",
			"Castelo Branco", "Castigliano", "Cathelain", "Celtic Promise", "Cendre d'Olivet",
			"Cerney", "Chabichou", "Chabichou du Poitou", "Chabis de Gatine", "Chaource",
			"Charolais", "Chaumes", "Cheddar", "Cheddar Clothbound", "Cheshire", "Chevres",
			"Chevrotin des Aravis", "Chontaleno", "Civray", "Coeur de Camembert au Calvados",
			"Coeur de Chevre", "Colby", "Cold Pack", "Comte", "Coolea", "Cooleney", "Coquetdale",
			"Corleggy", "Cornish Pepper", "Cotherstone", "Cotija", "Cottage Cheese",
			"Cottage Cheese (Australian)", "Cougar Gold", "Coulommiers", "Coverdale",
			"Crayeux de Roncq", "Cream Cheese", "Cream Havarti", "Crema Agria", "Crema Mexicana",
			"Creme Fraiche", "Crescenza", "Croghan", "Crottin de Chavignol",
			"Crottin du Chavignol", "Crowdie", "Crowley", "Cuajada", "Curd", "Cure Nantais",
			"Curworthy", "Cwmtawe Pecorino", "Cypress Grove Chevre", "Danablu (Danish Blue)",
			"Danbo", "Danish Fontina", "Daralagjazsky", "Dauphin", "Delice des Fiouves",
			"Denhany Dorset Drum", "Derby", "Dessertnyj Belyj", "Devon Blue", "Devon Garland",
			"Dolcelatte", "Doolin", "Doppelrhamstufel", "Dorset Blue Vinney", "Double Gloucester",
			"Double Worcester", "Dreux a la Feuille", "Dry Jack", "Duddleswell", "Dunbarra",
			"Dunlop", "Dunsyre Blue", "Duroblando", "Durrus", "Dutch Mimolette (Commissiekaas)",
			"Edam", "Edelpilz", "Emental Grand Cru", "Emlett", "Emmental", "Epoisses de Bourgogne",
			"Esbareich", "Esrom", "Etorki", "Evansdale Farmhouse Brie", "Evora De L'Alentejo",
			"Exmoor Blue", "Explorateur", "Feta", "Feta (Australian)", "Figue", "Filetta",
			"Fin-de-Siecle", "Finlandia Swiss", "Finn", "Fiore Sardo", "Fleur du Maquis",
			"Flor de Guia", "Flower Marie", "Folded", "Folded cheese with mint",
			"Fondant de Brebis", "Fontainebleau", "Fontal", "Fontina Val d'Aosta",
			"Formaggio di capra", "Fougerus", "Four Herb Gouda", "Fourme d' Ambert",
			"Fourme de Haute Loire", "Fourme de Montbrison", "Fresh Jack", "Fresh Mozzarella",
			"Fresh Ricotta", "Fresh Truffles", "Fribourgeois", "Friesekaas", "Friesian", "Friesla",
			"Frinault", "Fromage a Raclette", "Fromage Corse", "Fromage de Montagne de Savoie",
			"Fromage Frais", "Fruit Cream Cheese", "Frying Cheese", "Fynbo", "Gabriel",
			"Galette du Paludier", "Galette Lyonnaise", "Galloway Goat's Milk Gems", "Gammelost",
			"Gaperon a l'Ail", "Garrotxa", "Gastanberra", "Geitost", "Gippsland Blue", "Gjetost",
			"Gloucester", "Golden Cross", "Gorgonzola", "Gornyaltajski", "Gospel Green", "Gouda",
			"Goutu", "Gowrie", "Grabetto", "Graddost", "Grafton Village Cheddar", "Grana",
			"Grana Padano", "Grand Vatel", "Grataron d' Areches", "Gratte-Paille", "Graviera",
			"Greuilh", "Greve", "Gris de Lille", "Gruyere", "Gubbeen", "Guerbigny", "Halloumi",
			"Halloumy (Australian)", "Haloumi-Style Cheese", "Harbourne Blue", "Havarti",
			"Heidi Gruyere", "Hereford Hop", "Herrgardsost", "Herriot Farmhouse", "Herve",
			"Hipi Iti", "Hubbardston Blue Cow", "Hushallsost", "Iberico", "Idaho Goatster",
			"Idiazabal", "Il Boschetto al Tartufo", "Ile d'Yeu", "Isle of Mull", "Jarlsberg",
			"Jermi Tortes", "Jibneh Arabieh", "Jindi Brie", "Jubilee Blue", "Juustoleipa",
			"Kadchgall", "Kaseri", "Kashta", "Kefalotyri", "Kenafa", "Kernhem", "Kervella Affine",
			"Kikorangi", "King Island Cape Wickham Brie", "King River Gold", "Klosterkaese",
			"Knockalara", "Kugelkase", "L'Aveyronnais", "L'Ecir de l'Aubrac", "La Taupiniere",
			"La Vache Qui Rit", "Laguiole", "Lairobell", "Lajta", "Lanark Blue", "Lancashire",
			"Langres", "Lappi", "Laruns", "Lavistown", "Le Brin", "Le Fium Orbo", "Le Lacandou",
			"Le Roule", "Leafield", "Lebbene", "Leerdammer", "Leicester", "Leyden", "Limburger",
			"Lincolnshire Poacher", "Lingot Saint Bousquet d'Orb", "Liptauer", "Little Rydings",
			"Livarot", "Llanboidy", "Llanglofan Farmhouse", "Loch Arthur Farmhouse",
			"Loddiswell Avondale", "Longhorn", "Lou Palou", "Lou Pevre", "Lyonnais", "Maasdam",
			"Macconais", "Mahoe Aged Gouda", "Mahon", "Malvern", "Mamirolle", "Manchego",
			"Manouri", "Manur", "Marble Cheddar", "Marbled Cheeses", "Maredsous", "Margotin",
			"Maribo", "Maroilles", "Mascares", "Mascarpone", "Mascarpone (Australian)",
			"Mascarpone Torta", "Matocq", "Maytag Blue", "Meira", "Menallack Farmhouse",
			"Menonita", "Meredith Blue", "Mesost", "Metton (Cancoillotte)", "Meyer Vintage Gouda",
			"Mihalic Peynir", "Milleens", "Mimolette", "Mine-Gabhar", "Mini Baby Bells", "Mixte",
			"Molbo", "Monastery Cheeses", "Mondseer", "Mont D'or Lyonnais", "Montasio",
			"Monterey Jack", "Monterey Jack Dry", "Morbier", "Morbier Cru de Montagne",
			"Mothais a la Feuille", "Mozzarella", "Mozzarella (Australian)",
			"Mozzarella di Bufala", "Mozzarella Fresh, in water", "Mozzarella Rolls", "Munster",
			"Murol", "Mycella", "Myzithra", "Naboulsi", "Nantais", "Neufchatel",
			"Neufchatel (Australian)", "Niolo", "Nokkelost", "Northumberland", "Oaxaca",
			"Olde York", "Olivet au Foin", "Olivet Bleu", "Olivet Cendre",
			"Orkney Extra Mature Cheddar", "Orla", "Oschtjepka", "Ossau Fermier", "Ossau-Iraty",
			"Oszczypek", "Oxford Blue", "P'tit Berrichon", "Palet de Babligny", "Paneer", "Panela",
			"Pannerone", "Pant ys Gawn", "Parmesan (Parmigiano)", "Parmigiano Reggiano",
			"Pas de l'Escalette", "Passendale", "Pasteurized Processed", "Pate de Fromage",
			"Patefine Fort", "Pave d'Affinois", "Pave d'Auge", "Pave de Chirac", "Pave du Berry",
			"Pecorino", "Pecorino in Walnut Leaves", "Pecorino Romano", "Peekskill Pyramid",
			"Pelardon des Cevennes", "Pelardon des Corbieres", "Penamellera", "Penbryn",
			"Pencarreg", "Perail de Brebis", "Petit Morin", "Petit Pardou", "Petit-Suisse",
			"Picodon de Chevre", "Picos de Europa", "Piora", "Pithtviers au Foin",
			"Plateau de Herve", "Plymouth Cheese", "Podhalanski", "Poivre d'Ane", "Polkolbin",
			"Pont l'Eveque", "Port Nicholson", "Port-Salut", "Postel", "Pouligny-Saint-Pierre",
			"Pourly", "Prastost", "Pressato", "Prince-Jean", "Processed Cheddar", "Provolone",
			"Provolone (Australian)", "Pyengana Cheddar", "Pyramide", "Quark",
			"Quark (Australian)", "Quartirolo Lombardo", "Quatre-Vents", "Quercy Petit",
			"Queso Blanco", "Queso Blanco con Frutas --Pina y Mango", "Queso de Murcia",
			"Queso del Montsec", "Queso del Tietar", "Queso Fresco", "Queso Fresco (Adobera)",
			"Queso Iberico", "Queso Jalapeno", "Queso Majorero", "Queso Media Luna",
			"Queso Para Frier", "Queso Quesadilla", "Rabacal", "Raclette", "Ragusano", "Raschera",
			"Reblochon", "Red Leicester", "Regal de la Dombes", "Reggianito", "Remedou",
			"Requeson", "Richelieu", "Ricotta", "Ricotta (Australian)", "Ricotta Salata", "Ridder",
			"Rigotte", "Rocamadour", "Rollot", "Romano", "Romans Part Dieu", "Roncal", "Roquefort",
			"Roule", "Rouleau De Beaulieu", "Royalp Tilsit", "Rubens", "Rustinu", "Saaland Pfarr",
			"Saanenkaese", "Saga", "Sage Derby", "Sainte Maure", "Saint-Marcellin",
			"Saint-Nectaire", "Saint-Paulin", "Salers", "Samso", "San Simon", "Sancerre",
			"Sap Sago", "Sardo", "Sardo Egyptian", "Sbrinz", "Scamorza", "Schabzieger", "Schloss",
			"Selles sur Cher", "Selva", "Serat", "Seriously Strong Cheddar", "Serra da Estrela",
			"Sharpam", "Shelburne Cheddar", "Shropshire Blue", "Siraz", "Sirene", "Smoked Gouda",
			"Somerset Brie", "Sonoma Jack", "Sottocenare al Tartufo", "Soumaintrain",
			"Sourire Lozerien", "Spenwood", "Sraffordshire Organic", "St. Agur Blue Cheese",
			"Stilton", "Stinking Bishop", "String", "Sussex Slipcote", "Sveciaost", "Swaledale",
			"Sweet Style Swiss", "Swiss", "Syrian (Armenian String)", "Tala", "Taleggio", "Tamie",
			"Tasmania Highland Chevre Log", "Taupiniere", "Teifi", "Telemea", "Testouri",
			"Tete de Moine", "Tetilla", "Texas Goat Cheese", "Tibet", "Tillamook Cheddar",
			"Tilsit", "Timboon Brie", "Toma", "Tomme Brulee", "Tomme d'Abondance",
			"Tomme de Chevre", "Tomme de Romans", "Tomme de Savoie", "Tomme des Chouans", "Tommes",
			"Torta del Casar", "Toscanello", "Touree de L'Aubier", "Tourmalet",
			"Trappe (Veritable)", "Trois Cornes De Vendee", "Tronchon", "Trou du Cru", "Truffe",
			"Tupi", "Turunmaa", "Tymsboro", "Tyn Grug", "Tyning", "Ubriaco", "Ulloa",
			"Vacherin-Fribourgeois", "Valencay", "Vasterbottenost", "Venaco", "Vendomois",
			"Vieux Corse", "Vignotte", "Vulscombe", "Waimata Farmhouse Blue",
			"Washed Rind Cheese (Australian)", "Waterloo", "Weichkaese", "Wellington",
			"Wensleydale", "White Stilton", "Whitestone Farmhouse", "Wigmore", "Woodside Cabecou",
			"Xanadu", "Xynotyro", "Yarg Cornish", "Yarra Valley Pyramid", "Yorkshire Blue",
			"Zamorano", "Zanetti Grana Padano", "Zanetti Parmigiano Reggiano" };

}