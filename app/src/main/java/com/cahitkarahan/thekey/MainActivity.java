package com.cahitkarahan.thekey;

import com.cahitkarahan.thekey.SplashActivity;
import android.Manifest;
import android.animation.*;
import android.app.*;
import android.app.AlertDialog;
import android.content.*;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.*;
import android.graphics.*;
import android.graphics.Typeface;
import android.graphics.drawable.*;
import android.media.*;
import android.net.*;
import android.net.Uri;
import android.os.*;
import android.os.Bundle;
import android.text.*;
import android.text.style.*;
import android.util.*;
import android.view.*;
import android.view.View;
import android.view.View.*;
import android.view.animation.*;
import android.webkit.*;
import android.widget.*;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.*;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.io.InputStream;
import java.text.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.*;
import org.json.*;
import org.ocpsoft.prettytime.PrettyTime;
import java.io.File;

public class MainActivity extends AppCompatActivity {
	
	private Toolbar _toolbar;
	private AppBarLayout _app_bar;
	private CoordinatorLayout _coordinator;
	private FloatingActionButton _fab;
	private DrawerLayout _drawer;
	private double ran = 0;
	private String listspath = "";
	private HashMap<String, Object> listItem = new HashMap<>();
	private double activeListId = 0;
	private boolean error = false;
	private HashMap<String, Object> temp = new HashMap<>();
	private HashMap<String, Object> defaultHints = new HashMap<>();
	private double todoListPos = 0;
	private double listOfListsPos = 0;
	
	private ArrayList<String> letters = new ArrayList<>();
	private ArrayList<HashMap<String, Object>> activeList = new ArrayList<>();
	private ArrayList<String> ohListOfTheLists = new ArrayList<>();
	private ArrayList<String> timeOptions = new ArrayList<>();
	private ArrayList<HashMap<String, Object>> listOfTheLists = new ArrayList<>();
	
	private LinearLayout main_lay;
	private SwipeRefreshLayout swipe_refresh_main;
	private ListView todoList;
	private LinearLayout _drawer_header;
	private SwipeRefreshLayout _drawer_swipe_refresh;
	private TextView _drawer_credits;
	private LinearLayout _drawer_linear_title;
	private ImageView _drawer_btn_new_list;
	private TextView _drawer_title;
	private ListView _drawer_listOfLists;
	
	private Intent i1 = new Intent();
	private AlertDialog.Builder dialogMan;
	
	@Override
	protected void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		setContentView(R.layout.main);
		initialize(_savedInstanceState);
		
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
		|| ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
			ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
		} else {
			initializeLogic();
		}
	}
	
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == 1000) {
			initializeLogic();
		}
	}
	
	private void initialize(Bundle _savedInstanceState) {
		_app_bar = findViewById(R.id._app_bar);
		_coordinator = findViewById(R.id._coordinator);
		_toolbar = findViewById(R.id._toolbar);
		setSupportActionBar(_toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _v) {
				onBackPressed();
			}
		});
		_fab = findViewById(R.id._fab);
		
		_drawer = findViewById(R.id._drawer);
		ActionBarDrawerToggle _toggle = new ActionBarDrawerToggle(MainActivity.this, _drawer, _toolbar, R.string.app_name, R.string.app_name);
		_drawer.addDrawerListener(_toggle);
		_toggle.syncState();
		
		LinearLayout _nav_view = findViewById(R.id._nav_view);
		
		main_lay = findViewById(R.id.main_lay);
		swipe_refresh_main = findViewById(R.id.swipe_refresh_main);
		todoList = findViewById(R.id.todoList);
		_drawer_header = _nav_view.findViewById(R.id.header);
		_drawer_swipe_refresh = _nav_view.findViewById(R.id.swipe_refresh);
		_drawer_credits = _nav_view.findViewById(R.id.credits);
		_drawer_linear_title = _nav_view.findViewById(R.id.linear_title);
		_drawer_btn_new_list = _nav_view.findViewById(R.id.btn_new_list);
		_drawer_title = _nav_view.findViewById(R.id.title);
		_drawer_listOfLists = _nav_view.findViewById(R.id.listOfLists);
		dialogMan = new AlertDialog.Builder(this);
		
		swipe_refresh_main.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				_reload_active_list();
				swipe_refresh_main.setRefreshing(false);
			}
		});
		
		_fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				temp = new HashMap<>();
				temp.put("text", "");
				if (listOfTheLists.get((int)activeListId).containsKey("timeable")) {
					temp.put("time", "1");
				}
				if (listOfTheLists.get((int)activeListId).containsKey("reasonable")) {
					temp.put("reason", "");
				}
				if (listOfTheLists.get((int)activeListId).containsKey("becomeable")) {
					temp.put("who", "");
				}
				temp.put("date", (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")).format(new Date()));
				if (listOfTheLists.get((int)activeListId).containsKey("achievable")) {
					temp.put("isChecked", false);
				}
				if (listOfTheLists.get((int)activeListId).containsKey("priority_highlight")) {
					temp.put("isHighlighted", false);
				}
				temp.put("isExpanded", false);
				final com.google.android.material.bottomsheet.BottomSheetDialog dialogMan = new com.google.android.material.bottomsheet.BottomSheetDialog(MainActivity.this);
				
				View bottomSheetView; bottomSheetView = getLayoutInflater().inflate(R.layout.todo_add_edit,null );
				dialogMan.setContentView(bottomSheetView);
				
				dialogMan.getWindow().findViewById(R.id.design_bottom_sheet).setBackground(new ColorDrawable(0xFFFFFFFF));
				error = false;
				final EditText edit_thetext = (EditText) bottomSheetView.findViewById(R.id.edit_thetext);
				if (defaultHints.containsKey(listOfTheLists.get((int)activeListId).get("name").toString())) {
					edit_thetext.setHint(defaultHints.get(listOfTheLists.get((int)activeListId).get("name").toString()).toString());
				}
				else {
					edit_thetext.setHint(defaultHints.get("default").toString());
				}
				if (temp.containsKey("text")) {
					edit_thetext.setText(temp.get("text").toString());
				}
				else {
					edit_thetext.setVisibility(View.GONE);
				}
				final Spinner edit_time = (Spinner) bottomSheetView.findViewById(R.id.edit_time);
				final LinearLayout linear_edit_time = (LinearLayout) bottomSheetView.findViewById(R.id.linear_edit_time);
				if (temp.containsKey("time")) {
					edit_time.setAdapter(new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, timeOptions));
					edit_time.setGravity(Gravity.TOP | Gravity.LEFT);
					for (int i = 0; i < (int)(timeOptions.size()); i++) {
						if (timeOptions.get((int)(i)).contains(temp.get("time").toString().concat(" "))) {
							edit_time.setSelection(i);
						}
					}
				}
				else {
					linear_edit_time.setVisibility(View.GONE);
				}
				final EditText edit_reason = (EditText) bottomSheetView.findViewById(R.id.edit_reason);
				final LinearLayout linear_edit_reason = (LinearLayout) bottomSheetView.findViewById(R.id.linear_edit_reason);
				if (temp.containsKey("reason")) {
					edit_reason.setText(temp.get("reason").toString());
				}
				else {
					linear_edit_reason.setVisibility(View.GONE);
				}
				final EditText edit_who = (EditText) bottomSheetView.findViewById(R.id.edit_who);
				final LinearLayout linear_edit_who = (LinearLayout) bottomSheetView.findViewById(R.id.linear_edit_who);
				if (temp.containsKey("who")) {
					edit_who.setText(temp.get("who").toString());
				}
				else {
					linear_edit_who.setVisibility(View.GONE);
				}
				final Button btn_edit_ok = (Button) bottomSheetView.findViewById(R.id.btn_edit_ok);
				btn_edit_ok.setOnClickListener(new View.OnClickListener(){ public void onClick(View _view){
						if ((edit_thetext.getText().toString().length() > 0) && !error) {
							for (int k = 0; k < (int)(activeList.size()); k++) {
								if (activeList.get((int)k).get("text").toString().equals(edit_thetext.getText().toString())) {
									error = true;
									((EditText)edit_thetext).setError("This item already exists.");
								}
							}
							if (!error) {
								temp.put("text", edit_thetext.getText().toString());
							}
						}
						else {
							error = true;
							((EditText)edit_thetext).setError("Please fill this field.");
						}
						if (temp.containsKey("time") && !error) {
							temp.put("time", String.valueOf((long)(Double.parseDouble(edit_time.getSelectedItem().toString().replaceAll("[^\\d]", "")))));
						}
						if (temp.containsKey("reason") && !error) {
							temp.put("reason", edit_reason.getText().toString());
						}
						if (temp.containsKey("who") && !error) {
							temp.put("who", edit_who.getText().toString());
						}
						if (!error) {
							activeList.add(temp);
							_save_active_list();
							temp = new HashMap<>();
							dialogMan.dismiss();
						}
						error = false;
					}
				});
				final Button btn_edit_cancel = (Button) bottomSheetView.findViewById(R.id.btn_edit_cancel);
				btn_edit_cancel.setOnClickListener(new View.OnClickListener(){ public void onClick(View _view){
						dialogMan.dismiss();
					}
				});
				dialogMan.setCancelable(true);
				dialogMan.show();
			}
		});
		
		_drawer_swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				_load_all_lists(true);
				_drawer_swipe_refresh.setRefreshing(false);
			}
		});
		
		_drawer_btn_new_list.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				temp = new HashMap<>();
				temp.put("name", "");
				final com.google.android.material.bottomsheet.BottomSheetDialog dialogMan = new com.google.android.material.bottomsheet.BottomSheetDialog(MainActivity.this);
				
				View bottomSheetView; bottomSheetView = getLayoutInflater().inflate(R.layout.new_list,null );
				dialogMan.setContentView(bottomSheetView);
				
				dialogMan.getWindow().findViewById(R.id.design_bottom_sheet).setBackground(new ColorDrawable(0xFFFFFFFF));
				error = false;
				final EditText edit_list_name = (EditText) bottomSheetView.findViewById(R.id.edit_list_name);
				if (temp.containsKey("name")) {
					edit_list_name.setText(temp.get("name").toString());
				}
				else {
					edit_list_name.setVisibility(View.GONE);
				}
				final LinearLayout linear_edit_feats = (LinearLayout) bottomSheetView.findViewById(R.id.linear_edit_feats);
				View.OnClickListener featsListener = new View.OnClickListener () {
					  public void onClick (View v) {
							// Get the temp HashMap from the view tag
						    HashMap<String, Object> temp = (HashMap<String, Object>) v.getTag ();
						    // Get the id of the view that was clicked
						    int id = v.getId ();
						    // Cast the view to an ImageView
						    ImageView imageView = (ImageView) v;
						    // Get the drawable of the ImageView
						    Drawable drawable = imageView.getDrawable ();
						    // Check if the drawable is checkbox_empty or checkbox_checked
						    if (drawable.getConstantState ().equals(getResources ().getDrawable (R.drawable.checkbox_empty).getConstantState ())) {
							      // If empty, set it to checked
							      imageView.setImageResource (R.drawable.checkbox_checked);
							      // Check which ImageView was clicked and update the HashMap accordingly
							      if (id == R.id.check_A) {
								        temp.put ("achievable", true);
								      } else if (id == R.id.check_T) {
								        temp.put ("timeable", true);
								      } else if (id == R.id.check_R) {
								        temp.put ("reasonable", true);
								      } else if (id == R.id.check_B) {
								        temp.put ("becomeable", true);
								      } else if (id == R.id.check_P) {
								        temp.put ("priority_highlight", true);
								      }
							    } else {
							      // If checked, set it to empty
							      imageView.setImageResource (R.drawable.checkbox_empty);
							      // Check which ImageView was clicked and update the HashMap accordingly
							      if (id == R.id.check_A) {
								        temp.remove ("achievable");
								      } else if (id == R.id.check_T) {
								        temp.remove ("timeable");
								      } else if (id == R.id.check_R) {
								        temp.remove ("reasonable");
								      } else if (id == R.id.check_B) {
								        temp.remove ("becomeable");
								      } else if (id == R.id.check_P) {
								        temp.remove ("priority_highlight");
								      }
							    }
						  }
				};
				for (int i = 0; i < 5; i++) {
					  // Get the LinearLayout that contains the ImageView
					  LinearLayout linearLayout = (LinearLayout) linear_edit_feats.findViewById (getResources ().getIdentifier ("linear_feat_" + "ATRBP".charAt (i), "id", getPackageName ()));
					  // Get the ImageView from the LinearLayout
					  ImageView imageView = (ImageView) linearLayout.findViewById (getResources ().getIdentifier ("check_" + "ATRBP".charAt (i), "id", getPackageName ()));
					  // Set the listener to the ImageView
					  imageView.setOnClickListener (featsListener);
					  // Get the corresponding key from the temp HashMap based on the id of the ImageView
					  String key = "";
					  // Use an if-else statement instead of a switch statement
					  int id = imageView.getId ();
					  if (id == R.id.check_A) {
						    key = "achievable";
						  } else if (id == R.id.check_T) {
						    key = "timeable";
						  } else if (id == R.id.check_R) {
						    key = "reasonable";
						  } else if (id == R.id.check_B) {
						    key = "becomeable";
						  } else if (id == R.id.check_P) {
						    key = "priority_highlight";
						  }
					  // Check if the value of the key is true or false in the temp HashMap
					  Boolean value = (Boolean)temp.get (key);
					  if (value != null && value == true) {
						    // If true, set the image of the ImageView to checkbox_checked
						    imageView.setImageResource (R.drawable.checkbox_checked);
						  } else {
						    // If false or null, set the image of the ImageView to checkbox_empty
						    imageView.setImageResource (R.drawable.checkbox_empty);
						  }
					  
					  // Set the tag of the ImageView to the temp HashMap
					  imageView.setTag (temp);
				}
				final Button btn_edit_list_ok = (Button) bottomSheetView.findViewById(R.id.btn_edit_list_ok);
				btn_edit_list_ok.setTag(temp);
				btn_edit_list_ok.setOnClickListener(new View.OnClickListener(){ public void onClick(View _view){
						temp = (HashMap<String, Object>)_view.getTag();
						if ((edit_list_name.getText().toString().length() > 0) && !error) {
							for (int k = 0; k < (int)(listOfTheLists.size()); k++) {
								if (listOfTheLists.get((int)k).get("name").toString().equals(edit_list_name.getText().toString())) {
									error = true;
									((EditText)edit_list_name).setError("This list name is already exists.");
								}
							}
							if (!error) {
								temp.put("name", edit_list_name.getText().toString());
							}
						}
						else {
							error = true;
							((EditText)edit_list_name).setError("Please fill this field.");
						}
						if (!error) {
							String filename = "";
							// Get the name value from the temp HashMap
							String name = (String)temp.get ("name");
							// Append the name value to the filename variable
							filename = filename + name;
							// Define a string array with the keys of the feats
							String[] keys = {"achievable", "timeable", "reasonable", "becomeable", "priority_highlight"};
							// Iterate over the keys
							for (int i = 0; i < keys.length; i++) {
								  // Get the current key
								  String key = keys[i];
								  // Get the value of the key from the temp HashMap
								  Boolean value = (Boolean)temp.get (key);
								  // Check if the value is true or false
								  if (value != null && value == true) {
									    // If true, append the first letter of the key to the filename
									    filename = filename + "_" + Character.toUpperCase(key.charAt (0));
									  }
							}
							// Append ".json" to the filename variable
							filename = filename + ".json";
							FileUtil.writeFile(listspath.concat("/".concat(filename)), new Gson().toJson(new ArrayList<HashMap<String, Object>>()));
							_load_all_lists(true);
							for (int j = 0; j < (int)(listOfTheLists.size()); j++) {
								if (listOfTheLists.get((int)j).get("name").toString().equals(temp.get("name").toString())) {
									_load_list(j);
									_drawer_listOfLists.smoothScrollToPosition((int)(j));
									_drawer.closeDrawer(GravityCompat.START);
								}
							}
							dialogMan.dismiss();
						}
						error = false;
					}
				});
				final Button btn_edit_list_cancel = (Button) bottomSheetView.findViewById(R.id.btn_edit_list_cancel);
				btn_edit_list_cancel.setOnClickListener(new View.OnClickListener(){ public void onClick(View _view){
						dialogMan.dismiss();
					}
				});
				dialogMan.setCancelable(true);
				dialogMan.show();
			}
		});
	}
	
	private void initializeLogic() {
		/*
if (!MainActivity.this.getSignture(MainActivity.this).equals("FINALSIGNATUREOFAPP")) {

i1.putExtra("error", getApplicationContext().getPackageName());
startActivity(i1);
finish();
}
*/
		timeOptions.add("1 Year or less");
		timeOptions.add("3 Years");
		timeOptions.add("5 Years");
		timeOptions.add("10 Years or more");
		defaultHints.put("Goals", "What to do if everything went perfectly on the road?");
		defaultHints.put("Turn-Ons", "Which events turn me onto life?");
		defaultHints.put("Turn-Offs", "Which events turn me off from life?");
		defaultHints.put("Rules & Advices", "Which rules should I follow on my journey of life?");
		defaultHints.put("Help", "What should I know about the app and Jim Rohn's methodology?");
		defaultHints.put("default", "What's on my mind?");
		todoList.setAdapter(new TodoListAdapter(activeList));
		listspath = FileUtil.getExternalStorageDir().concat("/.thekey");
		FileUtil.makeDir(listspath);
		_load_all_lists(false);
		if (listOfTheLists.size() < 1) {
			_load_defaults();
		}
		else {
			_load_list(0);
			_drawer_listOfLists.setAdapter(new _drawer_listOfListsAdapter(listOfTheLists));
		}
		if (FileUtil.isExistFile(listspath.concat("/backup.zip")) && (Math.round((new File(listspath + "/backup.zip")).length() / (1024.0 * 1024.0)) > 128)) {
			FileUtil.deleteFile(listspath.concat("/backup.zip"));
			_toast_error_or_not("Size of the recursive historical backup file reached 128 MB. Deleting backup branch and starting a new one.", "");
		}
		if (!FileUtil.isExistFile(listspath.concat("/backup.zip"))) {
			_create_backup();
		}
		else {
			try{
				java.io.File file = new java.io.File(listspath.concat("/backup.zip"));
				Date lastModDate = new Date(file.lastModified());
				if (((new Date()).getTime() - lastModDate.getTime() >= 24 * 60 * 60 * 1000)) {
					_create_backup();
				}
			}catch(Exception e){
				_toast_error_or_not("Error while getting the date of the backup file. Manual backups recommended.", "error");
			}
		}
	}
	
	
	@Override
	public void onResume() {
		super.onResume();
		_reload_active_list();
	}
	
	@Override
	public void onBackPressed() {
		if (_drawer.isDrawerOpen(GravityCompat.START)) {
			_drawer.closeDrawer(GravityCompat.START);
		} else {
			super.onBackPressed();
		}
	}
	public void _toast_error_or_not(final String _text, final String _error) {
		//read the code and their comments  carefully
		//if you want an error toast enter string value as "error"
		LayoutInflater inflater = getLayoutInflater(); View toastLayout = inflater.inflate(R.layout.toast, null);
		
		TextView toast_text = (TextView) toastLayout.findViewById(R.id.toast_text);
		toast_text.setText(_text);
		LinearLayout toast_lay = (LinearLayout) toastLayout.findViewById(R.id.toast_lay);
		
		
		if (_error.equals("error")) {
			//error toast
			android.graphics.drawable.GradientDrawable gd = new android.graphics.drawable.GradientDrawable();
			toast_text.setTextColor(Color.parseColor("#F44336"));
			gd.setColor(Color.parseColor("#FFFFFF"));
			gd.setCornerRadius(20);
			gd.setStroke(3, Color.parseColor("#F44336"));
			toast_lay.setBackground(gd);
			
			
		}
		else {
			//ordinary toast
			android.graphics.drawable.GradientDrawable gd = new android.graphics.drawable.GradientDrawable();
			gd.setColor(Color.parseColor("#FFFFFF"));
			gd.setCornerRadius(20);
			gd.setStroke(3, Color.parseColor("#2196F3"));
			toast_lay.setBackground(gd);
			
			
		}
		Toast toast = new Toast(getApplicationContext()); toast.setDuration(Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.TOP,0,110);
		toast.setView(toastLayout);
		toast.show();
		//code created by androcub softwares
		//follow as on instagram
		//@androcub
	}
	
	
	public void _randomLetterGen(final double _lenght, final TextView _textview) {
		
		ran = SketchwareUtil.getRandom((int)(0), (int)(9999));
		
	}
	
	
	public void _Card_View(final View _view, final double _cornerradius, final String _bgcolor, final double _elevation, final double _stroke, final String _strokecolor) {
		if (_stroke == 0) {
			//𝐁𝐥𝐨𝐜𝐤 𝐜𝐫𝐞𝐚𝐭𝐞𝐝 𝐛𝐲 𝐇-6𝐢𝐱
			android.graphics.drawable.GradientDrawable cv = new android.graphics.drawable.GradientDrawable(); 
			float cornerradius = (float) _cornerradius;
			cv.setCornerRadius(cornerradius);
			cv.setColor(Color.parseColor("#" + _bgcolor.replace("#", "")));
			_view.setBackground(cv);
			float elevation = (float) _elevation;
			_view.setElevation(elevation);
		}
		else {
			android.graphics.drawable.GradientDrawable cv = new android.graphics.drawable.GradientDrawable(); 
			float cornerradius = (float) _cornerradius;
			cv.setStroke((int)_stroke, Color.parseColor("#" + _strokecolor.replace("#", "")));
			cv.setCornerRadius(cornerradius);
			cv.setColor(Color.parseColor("#" + _bgcolor.replace("#", "")));
			_view.setBackground(cv);
			float elevation = (float) _elevation;
			_view.setElevation(elevation);
		}
	}
	
	
	public void _DECLARATIONS() {
	}
	/*
//blocks by arab ware channel
//original code is not but it has been changed ( some part are coded by arab ware channel )
*/
	
		public String getSignture(Context context) {
		try {
					android.content.pm.PackageInfo packageInfo = context.getPackageManager().getPackageInfo(
							getPackageName(), android.content.pm.PackageManager.GET_SIGNATURES);
			    //note sample just checks the first signature
					for (android.content.pm.Signature signature : packageInfo.signatures) {
							// SHA1 the signature
							String sha1 = getSHA1_(signature.toByteArray());
							// check is matches hardcoded value
							return sha1;
					}
		} catch(android.content.pm.PackageManager.NameNotFoundException e) {}
		
			return "";
		}
	  
	  //computed the sha1 hash of the signature
	  public String getSHA1_(byte[] sig) {
		try {
			  		java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA1");
			digest.update(sig);
						byte[] hashtext = digest.digest();
						return bytes_To_Hex_(hashtext);
		} catch(java.security.NoSuchAlgorithmException e) {}
					return "";
		}
	  
	  //util method to convert byte array to hex string
	  public String bytes_To_Hex_(byte[] bytes) {
		  	final char[] hexArray = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
							'9', 'A', 'B', 'C', 'D', 'E', 'F' };
				char[] hexChars = new char[bytes.length * 2];
				int v;
				for (int j = 0; j < bytes.length; j++) {
						v = bytes[j] & 0xFF;
						hexChars[j * 2] = hexArray[v >>> 4];
						hexChars[j * 2 + 1] = hexArray[v & 0x0F];
				}
				return new String(hexChars);
		}
	  
	    
	
	{
	}
	
	
	/*MADE BY ARAB WARE CHANNEL*/
	/*WHAT IS MADE ? ===>ADD FOLDER TO ZIP*/
	
	public void ArabWareAddFolderToZip (String _from,String _to) {
		if (FileUtil.isExistFile(_to.replace(Uri.parse(_to).getLastPathSegment(), "").concat("files/"))) {
				new UnZip().unZipIt(_to, _to.replace(Uri.parse(_to).getLastPathSegment(), "").concat("files/"));
				new java.io.File(_from).renameTo(new java.io.File(_to.replace(Uri.parse(_to).getLastPathSegment(), "").concat("files/").concat(Uri.parse(_from).getLastPathSegment())));
				try {
						Zip.zipFolder(_to.replace(Uri.parse(_to).getLastPathSegment(), "").concat("files/"),_to.replace(Uri.parse(_to).getLastPathSegment(), "") + Uri.parse(_to).getLastPathSegment());
				} catch(Exception e) {}
				FileUtil.deleteFile(_to.replace(Uri.parse(_to).getLastPathSegment(), "").concat("files/"));
		}
		else {
				FileUtil.makeDir(_to.replace(Uri.parse(_to).getLastPathSegment(), "").concat("files/"));
				new UnZip().unZipIt(_to, _to.replace(Uri.parse(_to).getLastPathSegment(), "").concat("files/"));
				new java.io.File(_from).renameTo(new java.io.File(_to.replace(Uri.parse(_to).getLastPathSegment(), "").concat("files/").concat(Uri.parse(_from).getLastPathSegment())));
				try {
						Zip.zipFolder(_to.replace(Uri.parse(_to).getLastPathSegment(), "").concat("files/"),_to.replace(Uri.parse(_to).getLastPathSegment(), "") + Uri.parse(_to).getLastPathSegment());
				} catch(Exception e) {}
				FileUtil.deleteFile(_to.replace(Uri.parse(_to).getLastPathSegment(), "").concat("files/"));
		}
	}
	
	
	
	public void addFilesToZip(java.io.File source, java.io.File files)
	{
		    try
		    {
			
			   
			        byte[] buffer = new byte[1024];
			        java.util.zip.ZipInputStream zin = new java.util.zip.ZipInputStream(new java.io.FileInputStream(files));
			        java.util.zip.ZipOutputStream out = new java.util.zip.ZipOutputStream(new java.io.FileOutputStream(source));
			            java.io.InputStream in = new java.io.FileInputStream(files);
			            out.putNextEntry(new java.util.zip.ZipEntry(files.getName()));
			            for(int read = in.read(buffer); read > -1; read = in.read(buffer))
			            {
				                out.write(buffer, 0, read);
				            }
			            out.closeEntry();
			            in.close();
			
			        for(java.util.zip.ZipEntry ze = zin.getNextEntry(); ze != null; ze = zin.getNextEntry())
			        {
				            out.putNextEntry(ze);
				            for(int read = zin.read(buffer); read > -1; read = zin.read(buffer))
				            {
					                out.write(buffer, 0, read);
					            }
				            out.closeEntry();
				        }
			
			        out.close();
			      
			    }
		    catch(Exception e)
		    {
			       showMessage(e.getMessage());
			    }
	}
	public static class Zip {
		
		public static void zipFolder(String str, String str2) throws Exception {
			            java.util.zip.ZipOutputStream zipOutputStream = new java.util.zip.ZipOutputStream(new java.io.FileOutputStream(str2));
			            addFolderToZip("", str, zipOutputStream);
			            zipOutputStream.flush();
			            zipOutputStream.close();
			        }
		
		
		public static void addFolderToZip(String str, String str2, java.util.zip.ZipOutputStream zipOutputStream) throws Exception {
			            java.io.File file = new java.io.File(str2);
			            for (String str3 : file.list()) {
				                if (str.equals("")) {
					                    addFileToZip(file.getName(), new StringBuilder(String.valueOf(str2)).append("/").append(str3).toString(), zipOutputStream);
					                } else {
					                    addFileToZip(new StringBuilder(String.valueOf(str)).append("/").append(file.getName()).toString(), new StringBuilder(String.valueOf(str2)).append("/").append(str3).toString(), zipOutputStream);
					                }
				            }
			        }
		
		
		
		  public static void addFileToZip(String path, String srcFile, java.util.zip.ZipOutputStream zip)
		      throws Exception {
			    java.io.File folder = new java.io.File(srcFile);
			    if (folder.isDirectory()) {
				      
				    } else {
				      byte[] buf = new byte[1024];
				      int len;
				      java.io.FileInputStream in = new java.io.FileInputStream(srcFile);
				      zip.putNextEntry(new java.util.zip.ZipEntry(path + "/" + folder.getName()));
				      while ((len = in.read(buf)) > 0) {
					        zip.write(buf, 0, len);
					      }
				    }
		}
		  }
	
	public static class UnZip {
		List<String> fileList;
		
		public void unZipIt(String zipFile, String outputFolder){
			byte[] buffer = new byte[1024];
			try{
				java.util.zip.ZipInputStream zis = new java.util.zip.ZipInputStream(new java.io.FileInputStream(zipFile));
				java.util.zip.ZipEntry ze = zis.getNextEntry();
				while(ze!=null){
					String fileName = ze.getName();
					java.io.File newFile = new java.io.File(outputFolder + java.io.File.separator + fileName);
					new java.io.File(newFile.getParent()).mkdirs();
					java.io.FileOutputStream fos = new java.io.FileOutputStream(newFile);
					int len;
					while ((len = zis.read(buffer)) > 0) {
						fos.write(buffer, 0, len);
					}
					fos.close();
					ze = zis.getNextEntry(); 
				}
				zis.closeEntry();
				zis.close();
			}catch(java.io.IOException ex){
				ex.printStackTrace();
			}
		}
	}
	
	
	public void _load_defaults() {
		String defaultsPath = "defaults_".concat(Locale.getDefault().getLanguage().concat(".zip"));
		try{
			MainActivity.this.getAssets().open(defaultsPath);
		}catch(Exception e){
			defaultsPath  = "defaults.zip";
		}
		{
			try{
				int count;
				java.io.InputStream input= MainActivity.this.getAssets().open(defaultsPath);
				java.io.OutputStream output = new  java.io.FileOutputStream(listspath + "/backup.zip");
				byte data[] = new byte[1024];
				while ((count = input.read(data))>0) {
					output.write(data, 0, count);
				}
				output.flush();
				output.close();
				input.close();
				
				}catch(Exception e){
						
				}
		}
		new UnZip().unZipIt(listspath.concat("/backup.zip"), listspath);
		_load_all_lists(true);
		_load_list(0);
	}
	
	
	public void _save_list(final String _path) {
		_sort_active_list();
		FileUtil.writeFile(_path, new Gson().toJson(activeList));
		_load_all_lists(true);
		_load_list(activeListId);
	}
	
	
	public void _save_active_list() {
		_save_list(ohListOfTheLists.get((int)(activeListId)));
	}
	
	
	public void _load_list(final double _id) {
		todoListPos = todoList.getFirstVisiblePosition();
		int topPos = (todoList.getChildAt(0)) == null ? 0 : (todoList.getChildAt(0).getTop() - todoList.getPaddingTop());
		if (FileUtil.isExistFile(ohListOfTheLists.get((int)(_id))) && FileUtil.isFile(ohListOfTheLists.get((int)(_id)))) {
			try{
				activeList = new Gson().fromJson(FileUtil.readFile(ohListOfTheLists.get((int)(_id))), new TypeToken<ArrayList<HashMap<String, Object>>>(){}.getType());
				activeListId = _id;
				todoList.setAdapter(new TodoListAdapter(activeList));
				_drawer_listOfLists.setAdapter(new _drawer_listOfListsAdapter(listOfTheLists));
				if (!temp.isEmpty()) {
					todoList.setSelection((int)activeList .indexOf(temp));
				}
				else {
					todoList.setSelectionFromTop((int)todoListPos, topPos);
				}
			}catch(Exception e){
				_toast_error_or_not("The list is corrupted!", "error");
			}
		}
		else {
			_toast_error_or_not("The list is not found!", "error");
		}
	}
	
	
	public void _sort_active_list() {
		// Comparator
		Comparator<HashMap<String,Object>> comparator = new Comparator<HashMap<String,Object>>() {
			    @Override
			    public int compare(HashMap<String,Object> map1, HashMap<String,Object> map2) {
				        // Sort by isChecked
				        boolean isChecked1 = false; // default value
				        boolean isChecked2 = false; // default value
				        if (map1.containsKey("isChecked")) { // check if key exists
					            isChecked1 = (boolean) map1.get("isChecked"); // get value
					        }
				        if (map2.containsKey("isChecked")) { // check if key exists
					            isChecked2 = (boolean) map2.get("isChecked"); // get value
					        }
				        int isCheckedComparison = Boolean.compare(isChecked1, isChecked2);
				        if (isCheckedComparison != 0) {
					            return isCheckedComparison; // return if not equal
					        }
				
				        // Sort by isHighlighted
				        boolean isHighlighted1 = false; // default value
				        boolean isHighlighted2 = false; // default value
				        if (map1.containsKey("isHighlighted")) { // check if key exists
					            isHighlighted1 = (boolean) map1.get("isHighlighted"); // get value
					        }
				        if (map2.containsKey("isHighlighted")) { // check if key exists
					            isHighlighted2 = (boolean) map2.get("isHighlighted"); // get value
					        }
				        int isHighlightedComparison = Boolean.compare(isHighlighted1, isHighlighted2);
				        if (isHighlightedComparison != 0) {
					            return isHighlightedComparison * -1; // return if not equal
					        }
				
				        // Sort by remaining time
				        long remainingTime1 = Long.MAX_VALUE; // default value
				        long remainingTime2 = Long.MAX_VALUE; // default value
				
				        try { // use try-catch block to catch any exceptions
					
					            String time1 = "0"; // default value
					            String time2 = "0"; // default value
					            String date1 = null; // default value
					            String date2 = null; // default value
					
					            if (map1.containsKey("time")) { // check if key exists
						                time1 = (String) map1.get("time"); // get value
						            }
					            if (map2.containsKey("time")) { // check if key exists
						                time2 = (String) map2.get("time"); // get value
						            }
					            if (map1.containsKey("date")) { // check if key exists
						                date1 = (String) map1.get("date"); // get value
						            }
					            if (map2.containsKey("date")) { // check if key exists
						                date2 = (String) map2.get("date"); // get value
						            }
					
					            double timeInYears1 = Double.parseDouble(time1);
					            double timeInYears2 = Double.parseDouble(time2);
					
					            long timeInMillis1 = Math.round(timeInYears1 * 365 * 24 * 60 * 60 * 1000);
					            long timeInMillis2 = Math.round(timeInYears2 * 365 * 24 * 60 * 60 * 1000);
					
					            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
					
					            Date dateObject1 = null;
					            Date dateObject2 = null;
					
					            if (date1 != null) { // check if date is not null
						                dateObject1 = dateFormat.parse(date1); // parse date string to Date object
						            }
					            if (date2 != null) { // check if date is not null
						                dateObject2 = dateFormat.parse(date2); // parse date string to Date object
						            }
					
					            long dateInMillis1 = 0;
					            long dateInMillis2 = 0;
					
					            if (dateObject1 != null) { // check if Date object is not null
						                dateInMillis1 = dateObject1.getTime(); // get time in milliseconds from Date object
						            }
					            if (dateObject2 != null) { // check if Date object is not null
						                dateInMillis2 = dateObject2.getTime(); // get time in milliseconds from Date object
						            }
					
					            remainingTime1 = timeInMillis1 - (System.currentTimeMillis() - dateInMillis1);
					            remainingTime2 = timeInMillis2 - (System.currentTimeMillis() - dateInMillis2);
					        } catch (NumberFormatException | java.text.ParseException e) { // catch any number format or parse exceptions
					            e.printStackTrace(); // print the stack trace for debugging
					            return 0; // return zero if comparison cannot be done
					        }
				
				        // Compare
				        int remainingTimeComparison = Long.compare(remainingTime1, remainingTime2); // use Integer.compare method instead of if-else statements
				        if (remainingTimeComparison != 0) {
					            return remainingTimeComparison; // return if not equal
					        }
				
				        // Sort by text alphabetically
				        String text1 = ""; // default value
				        String text2 = ""; // default value
				        if (map1.containsKey("text")) { // check if key exists
					            text1 = (String) map1.get("text"); // get value
					        }
				        if (map2.containsKey("text")) { // check if key exists
					            text2 = (String) map2.get("text"); // get value
					        }
				        return text1.compareTo(text2); // use String.compareTo method to compare the texts alphabetically
				    }
		};
		Collections.sort(activeList, comparator);
	}
	
	
	public void _reload_active_list() {
		_load_list(activeListId);
	}
	
	
	public ArrayList<HashMap<String, Object>> _parseListOfLists(final ArrayList<String> _listOfLists) {
		ArrayList<HashMap<String, Object>> dataOfLists = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < (int)(_listOfLists.size()); i++) {
			HashMap<String, Object> data = new HashMap<String, Object>();
			String fileName = Uri.parse(_listOfLists.get((int)(i))).getLastPathSegment();
			fileName = fileName.substring((int)(0), (int)(fileName.lastIndexOf(".")));
			ArrayList<String> listProps;
			listProps = new ArrayList<String>(Arrays.asList(fileName.split("_")));
			if (listProps.contains("A")) {
				data.put("achievable", true);
				listProps.remove((int)(listProps.indexOf("A")));
			}
			if (listProps.contains("T")) {
				data.put("timeable", true);
				listProps.remove((int)(listProps.indexOf("T")));
			}
			if (listProps.contains("R")) {
				data.put("reasonable", true);
				listProps.remove((int)(listProps.indexOf("R")));
			}
			if (listProps.contains("B")) {
				data.put("becomeable", true);
				listProps.remove((int)(listProps.indexOf("B")));
			}
			if (listProps.contains("P")) {
				data.put("priority_highlight", true);
				listProps.remove((int)(listProps.indexOf("P")));
			}
			try{
				java.io.File file = new java.io.File(_listOfLists.get((int)(i)));
				Date lastModDate = new Date(file.lastModified());
				data.put("date", (new PrettyTime(Locale.getDefault())).format(lastModDate));
			}catch(Exception e){
				_toast_error_or_not("Can't access the date of the list file. Skipping.", "");
			}
			ArrayList<HashMap<String, Object>> listItems = new ArrayList<HashMap<String, Object>>();
			listItems = new Gson().fromJson(FileUtil.readFile(ohListOfTheLists.get((int)(i))), new TypeToken<ArrayList<HashMap<String, Object>>>(){}.getType());
			int numTotal = listItems.size();
			data.put("numTotal", String.valueOf((long)(numTotal)));
			int numDone = 0;
			double timeTotal = 0;
			for (int j = 0; j < (int)(listItems.size()); j++) {
				if (listItems.get((int)j).containsKey("isChecked") && (boolean)listItems.get((int)(j)).get("isChecked")) {
					numDone = numDone + 1;
				}
				if (listItems.get((int)j).containsKey("time")) {
					timeTotal = timeTotal + Double.parseDouble(listItems.get((int)j).get("time").toString());
				}
			}
			if (data.containsKey("achievable")) {
				data.put("numDone", String.valueOf((long)(numDone)));
				data.put("donePercent", String.valueOf((long)((((float)numDone) / ((float)numTotal)) * 100)));
			}
			if (data.containsKey("timeable") && (numTotal > 0)) {
				data.put("avgTime", new DecimalFormat("#.##").format(timeTotal / numTotal));
			}
			data.put("name", listProps.get((int)(0)));
			dataOfLists.add(data);
		}
		return dataOfLists;
	}
	
	
	public void _load_all_lists(final boolean _reloadView) {
		listOfListsPos = _drawer_listOfLists.getFirstVisiblePosition();
		int topPos = (_drawer_listOfLists.getChildAt(0)) == null ? 0 : (_drawer_listOfLists.getChildAt(0).getTop() - _drawer_listOfLists.getPaddingTop());
		FileUtil.listDir(listspath, ohListOfTheLists);
		if (ohListOfTheLists.contains(listspath.concat("/backup.zip"))) {
			ohListOfTheLists.remove((int)(ohListOfTheLists.indexOf(listspath.concat("/backup.zip"))));
		}
		listOfTheLists = _parseListOfLists(ohListOfTheLists);
		if (_reloadView) {
			_drawer_listOfLists.setAdapter(new _drawer_listOfListsAdapter(listOfTheLists));
			_drawer_listOfLists.setSelectionFromTop((int)listOfListsPos, topPos);
		}
	}
	
	
	public void _MB_ListOfListsAdapter(final ArrayList<HashMap<String, Object>> _data, final double _position) {
	}
	//Definition blocks start
	
	public class _drawer_listOfListsAdapter extends BaseAdapter {
			ArrayList<HashMap<String, Object>> _data;
			public _drawer_listOfListsAdapter(ArrayList<HashMap<String, Object>> _arr) {
					_data = _arr;
			}
			
			@Override
			public int getCount() {
					return _data.size();
			}
			
			@Override
			public HashMap<String, Object> getItem(int _index) {
					return _data.get(_index);
			}
			
			@Override
			public long getItemId(int _index) {
					return _index;
			}
			@Override
			public View getView(final int _position, View _v, ViewGroup _container) {
					LayoutInflater _inflater = (LayoutInflater)getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					View _view = _v;
					if (_view == null) {
							_view = _inflater.inflate(R.layout.list_of_lists_item, null);
					}
			
			final LinearLayout linear_list_container = (LinearLayout) _view.findViewById(R.id.linear_list_container);
			final TextView list_name = (TextView) _view.findViewById(R.id.list_name);
			final TextView list_date = (TextView) _view.findViewById(R.id.list_date);
			final LinearLayout linear_list_main_item = (LinearLayout) _view.findViewById(R.id.linear_list_main_item);
			final ImageView list_show_more = (ImageView) _view.findViewById(R.id.list_show_more);
			final LinearLayout linear_list_more = (LinearLayout) _view.findViewById(R.id.linear_list_more);
			final TextView feats = (TextView) _view.findViewById(R.id.feats);
			final TextView stats = (TextView) _view.findViewById(R.id.stats);
			linear_list_more.setVisibility(View.GONE);
			linear_list_more.setEnabled(false);
			list_show_more.setImageResource(R.drawable.ic_arrow_drop_down_black);
			list_show_more.setOnClickListener(new View.OnClickListener(){ public void onClick(View _view){
					if (linear_list_more.isEnabled()) {
						linear_list_more.setVisibility(View.GONE);
						linear_list_more.setEnabled(false);
						list_show_more.setImageResource(R.drawable.ic_arrow_drop_down_black);
					}
					else {
						linear_list_more.setVisibility(View.VISIBLE);
						linear_list_more.setEnabled(true);
						list_show_more.setImageResource(R.drawable.ic_arrow_drop_up_black);
					}
				}
			});
			if (!(activeListId == _position)) {
				list_name.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/roboto.ttf"), 0);
				linear_list_container.setTag(_position);
				linear_list_container.setOnClickListener(new View.OnClickListener(){ public void onClick(View _view){
						_load_list((int)_view.getTag());
						_drawer.closeDrawer(GravityCompat.START);
					}
				});
				linear_list_container.setOnLongClickListener(new View.OnLongClickListener() {
					@Override
					public boolean onLongClick(View _view) {
						ran = (int)_view.getTag();
						AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
						builder.setCancelable(true);
						builder.setTitle("Confirm");
						builder.setMessage("Danger: You are going to delete a list completely.\nAre you sure that you want to delete this list completely?");
						builder.setPositiveButton("Delete",
						new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									//POSITIVE
								FileUtil.deleteFile(ohListOfTheLists.get((int)(ran)));
								_load_all_lists(true);
								SketchwareUtil.CustomToastWithIcon(getApplicationContext(), "The list is successfully deleted.  ", 0xFFFFFFFF, 12, 0xFF607D8B, 360, SketchwareUtil.BOTTOM, R.drawable.ic_info_outline_white);
							}
						});
						builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
										//NEGATIVE
								}
						});
						
						AlertDialog dialog = builder.create();
						dialog.show();
						return true;
					}
				});
			}
			else {
				linear_list_container.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View _view) {
						SketchwareUtil.CustomToastWithIcon(getApplicationContext(), "The list is already selected.  ", 0xFFFFFFFF, 12, 0xFF607D8B, 360, SketchwareUtil.BOTTOM, R.drawable.ic_info_outline_white);
					}
				});
				linear_list_container.setOnLongClickListener(new View.OnLongClickListener() {
					@Override
					public boolean onLongClick(View _view) {
						SketchwareUtil.CustomToastWithIcon(getApplicationContext(), "Can't edit or delete. The list is in use. Please deselect the list.  ", 0xFFFFFFFF, 12, 0xFF607D8B, 360, SketchwareUtil.BOTTOM, R.drawable.ic_info_outline_white);
						return true;
					}
				});
				list_name.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/roboto.ttf"), 1);
			}
			list_name.setText("🔹 ".concat(_data.get((int)_position).get("name").toString()));
			if (_data.get((int)_position).containsKey("date")) {
				list_date.setText(_data.get((int)_position).get("date").toString());
			}
			String list_feats = "";
			if (_data.get((int)_position).containsKey("achievable")) {
				list_feats = list_feats.concat("🔹 Achievable");
			}
			if (_data.get((int)_position).containsKey("timeable")) {
				list_feats = list_feats.concat("\n🔹 Timeable");
			}
			if (_data.get((int)_position).containsKey("reasonable")) {
				list_feats = list_feats.concat("\n🔹 Reasonable");
			}
			if (_data.get((int)_position).containsKey("becomeable")) {
				list_feats = list_feats.concat("\n🔹 Becomeable");
			}
			if (_data.get((int)_position).containsKey("priority_highlight")) {
				list_feats = list_feats.concat("\n🔹 Priority Highlight");
			}
			feats.setText(list_feats.trim().length() > 0 ? list_feats.trim() : "None");
			String list_stats = "🔹 Items: ";
			if (_data.get((int)_position).containsKey("numDone")) {
				list_stats = list_stats.concat(_data.get((int)_position).get("numDone").toString().concat("/".concat(_data.get((int)_position).get("numTotal").toString())).concat("\n🔹 ".concat(_data.get((int)_position).get("donePercent").toString().concat("% Done"))));
			}
			else {
				list_stats = list_stats.concat(_data.get((int)_position).get("numTotal").toString());
			}
			if (_data.get((int)_position).containsKey("avgTime")) {
				list_stats = list_stats.concat("\n🔹 Avg. Time: ".concat(_data.get((int)_position).get("avgTime").toString().concat(_data.get((int)_position).get("avgTime").toString().equals("1") ? " Year" : " Years")));
			}
			stats.setText(list_stats.trim().length() > 0 ? list_stats.trim() : "None");
			
					return _view;
			}
	}
	//Definition blocks end
	{
	}
	
	
	public void _create_backup() {
		try {
			Zip.zipFolder(listspath,listspath + "/backup.zip");
		} catch(Exception e) {}
		_toast_error_or_not("Automatic recursive historical backup saved to: ".concat(listspath.concat("/backup.zip")), "");
	}
	
	public class TodoListAdapter extends BaseAdapter {
		
		ArrayList<HashMap<String, Object>> _data;
		
		public TodoListAdapter(ArrayList<HashMap<String, Object>> _arr) {
			_data = _arr;
		}
		
		@Override
		public int getCount() {
			return _data.size();
		}
		
		@Override
		public HashMap<String, Object> getItem(int _index) {
			return _data.get(_index);
		}
		
		@Override
		public long getItemId(int _index) {
			return _index;
		}
		
		@Override
		public View getView(final int _position, View _v, ViewGroup _container) {
			LayoutInflater _inflater = getLayoutInflater();
			View _view = _v;
			if (_view == null) {
				_view = _inflater.inflate(R.layout.todo, null);
			}
			
			final LinearLayout linear_container = _view.findViewById(R.id.linear_container);
			final LinearLayout linear_main_item = _view.findViewById(R.id.linear_main_item);
			final LinearLayout linear_more = _view.findViewById(R.id.linear_more);
			final ImageView check_toggle = _view.findViewById(R.id.check_toggle);
			final LinearLayout linear2 = _view.findViewById(R.id.linear2);
			final TextView thetext = _view.findViewById(R.id.thetext);
			final ImageView show_more = _view.findViewById(R.id.show_more);
			final LinearLayout linear_time = _view.findViewById(R.id.linear_time);
			final LinearLayout linear_reason = _view.findViewById(R.id.linear_reason);
			final LinearLayout linear_who = _view.findViewById(R.id.linear_who);
			final LinearLayout linear_controls = _view.findViewById(R.id.linear_controls);
			final TextView date = _view.findViewById(R.id.date);
			final TextView header_time = _view.findViewById(R.id.header_time);
			final TextView time = _view.findViewById(R.id.time);
			final TextView header_reason = _view.findViewById(R.id.header_reason);
			final TextView reason = _view.findViewById(R.id.reason);
			final TextView header_who = _view.findViewById(R.id.header_who);
			final TextView who = _view.findViewById(R.id.who);
			final com.google.android.material.button.MaterialButton btn_edit = _view.findViewById(R.id.btn_edit);
			final com.google.android.material.button.MaterialButton btn_delete = _view.findViewById(R.id.btn_delete);
			
			btn_edit.setBackgroundTintList(ColorStateList.valueOf(0xFF2196F3));
			btn_delete.setBackgroundTintList(ColorStateList.valueOf(0xFFD50000));
			listItem = activeList.get((int)_position);
			if (listItem.isEmpty()) {
				_reload_active_list();
				return _view;
			}
			if (listItem.containsKey("text") && (listItem.get("text").toString().length() > 0)) {
				thetext.setText(listItem.get("text").toString());
			}
			if (listItem.containsKey("isHighlighted") && (boolean)listItem.get("isHighlighted")) {
				thetext.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/roboto.ttf"), 1);
			}
			else {
				thetext.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/roboto.ttf"), 0);
			}
			if (listItem.containsKey("isChecked") && (boolean)listItem.get("isChecked")) {
				check_toggle.setImageResource(R.drawable.checkbox_checked);
			}
			else {
				check_toggle.setImageResource(R.drawable.checkbox_empty);
			}
			if (!listOfTheLists.get((int)activeListId).containsKey("achievable")) {
				check_toggle.setVisibility(View.GONE);
			}
			if (listItem.containsKey("time") && (listItem.get("time").toString().length() > 0)) {
				time.setText(listItem.get("time").toString().concat(Double.parseDouble(listItem.get("time").toString()) > 1 ? Double.parseDouble(listItem.get("time").toString()) > 9 ? " Years or more" : " Years" : " Year or less"));
			}
			else {
				linear_time.setVisibility(View.GONE);
			}
			if (listItem.containsKey("reason") && (listItem.get("reason").toString().length() > 0)) {
				reason.setText(listItem.get("reason").toString());
			}
			else {
				linear_reason.setVisibility(View.GONE);
			}
			if (listItem.containsKey("who") && (listItem.get("who").toString().length() > 0)) {
				who.setText(listItem.get("who").toString());
			}
			else {
				linear_who.setVisibility(View.GONE);
			}
			if (listItem.containsKey("date") && (listItem.get("date").toString().length() > 0)) {
				try{
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");  
					
					Date dateCreated = dateFormat.parse(listItem.get("date").toString());
					date.setText((new PrettyTime(Locale.getDefault())).format(dateCreated));
				}catch(Exception e){
					_toast_error_or_not("Can't parse the date. Save files could be corrupted.", "error");
				}
			}
			else {
				date.setVisibility(View.GONE);
			}
			if (listItem.containsKey("isExpanded") && (boolean)listItem.get("isExpanded")) {
				linear_more.setVisibility(View.VISIBLE);
				show_more.setImageResource(R.drawable.ic_arrow_drop_up_black);
			}
			else {
				linear_more.setVisibility(View.GONE);
				show_more.setImageResource(R.drawable.ic_arrow_drop_down_black);
			}
			linear_main_item.setTag(listItem);
			check_toggle.setTag(listItem);
			btn_delete.setTag(listItem);
			btn_edit.setTag(listItem);
			linear_main_item.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View _view) {
					listItem = (HashMap<String, Object>)_view.getTag();
					if (listItem.containsKey("isExpanded") && (boolean)listItem.get("isExpanded")) {
						listItem.put("isExpanded", false);
					}
					else {
						listItem.put("isExpanded", true);
					}
					_save_active_list();
				}
			});
			check_toggle.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View _view) {
					listItem = (HashMap<String, Object>)_view.getTag();
					if (listItem.containsKey("isChecked") && (boolean)listItem.get("isChecked")) {
						listItem.put("isChecked", false);
					}
					else {
						listItem.put("isChecked", true);
					}
					_save_active_list();
				}
			});
			if (listOfTheLists.get((int)activeListId).containsKey("priority_highlight")) {
				linear_main_item.setOnLongClickListener(new View.OnLongClickListener() {
					@Override
					public boolean onLongClick(View _view) {
						listItem = (HashMap<String, Object>)_view.getTag();
						if (listItem.containsKey("isHighlighted") && (boolean)listItem.get("isHighlighted")) {
							listItem.put("isHighlighted", false);
						}
						else {
							listItem.put("isHighlighted", true);
						}
						_save_active_list();
						return true;
					}
				});
			}
			btn_delete.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View _view) {
					listItem = (HashMap<String, Object>)_view.getTag();
					AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
					builder.setCancelable(true);
					builder.setTitle("Confirm");
					builder.setMessage("Do you really want to delete the list item?");
					builder.setPositiveButton("Delete",
					new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								//POSITIVE
							activeList.remove(listItem);
							_save_active_list();
							SketchwareUtil.CustomToastWithIcon(getApplicationContext(), "List item is successfully deleted.  ", 0xFFFFFFFF, 12, 0xFF607D8B, 360, SketchwareUtil.BOTTOM, R.drawable.ic_info_outline_white);
						}
					});
					builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
									//NEGATIVE
							}
					});
					
					AlertDialog dialog = builder.create();
					dialog.show();
				}
			});
			btn_edit.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View _view) {
					listItem = (HashMap<String, Object>)_view.getTag();
					final com.google.android.material.bottomsheet.BottomSheetDialog dialogMan = new com.google.android.material.bottomsheet.BottomSheetDialog(MainActivity.this);
					
					View bottomSheetView; bottomSheetView = getLayoutInflater().inflate(R.layout.todo_add_edit,null );
					dialogMan.setContentView(bottomSheetView);
					
					dialogMan.getWindow().findViewById(R.id.design_bottom_sheet).setBackground(new ColorDrawable(0xFFFFFFFF));
					final EditText edit_thetext = (EditText) bottomSheetView.findViewById(R.id.edit_thetext);
					if (defaultHints.containsKey(listOfTheLists.get((int)activeListId).get("name").toString())) {
						edit_thetext.setHint(defaultHints.get(listOfTheLists.get((int)activeListId).get("name").toString()).toString());
					}
					else {
						edit_thetext.setHint(defaultHints.get("default").toString());
					}
					if (listItem.containsKey("text")) {
						edit_thetext.setText(listItem.get("text").toString());
					}
					else {
						edit_thetext.setVisibility(View.GONE);
					}
					final Spinner edit_time = (Spinner) bottomSheetView.findViewById(R.id.edit_time);
					final LinearLayout linear_edit_time = (LinearLayout) bottomSheetView.findViewById(R.id.linear_edit_time);
					if (listItem.containsKey("time")) {
						edit_time.setAdapter(new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, timeOptions));
						edit_time.setGravity(Gravity.TOP | Gravity.LEFT);
						for (int i = 0; i < (int)(timeOptions.size()); i++) {
							if (timeOptions.get((int)(i)).contains(listItem.get("time").toString().concat(" "))) {
								edit_time.setSelection(i);
							}
						}
					}
					else {
						linear_edit_time.setVisibility(View.GONE);
					}
					final EditText edit_reason = (EditText) bottomSheetView.findViewById(R.id.edit_reason);
					final LinearLayout linear_edit_reason = (LinearLayout) bottomSheetView.findViewById(R.id.linear_edit_reason);
					if (listItem.containsKey("reason")) {
						edit_reason.setText(listItem.get("reason").toString());
					}
					else {
						linear_edit_reason.setVisibility(View.GONE);
					}
					final EditText edit_who = (EditText) bottomSheetView.findViewById(R.id.edit_who);
					final LinearLayout linear_edit_who = (LinearLayout) bottomSheetView.findViewById(R.id.linear_edit_who);
					if (listItem.containsKey("who")) {
						edit_who.setText(listItem.get("who").toString());
					}
					else {
						linear_edit_who.setVisibility(View.GONE);
					}
					final Button btn_edit_ok = (Button) bottomSheetView.findViewById(R.id.btn_edit_ok);
					btn_edit_ok.setTag(listItem);
					btn_edit_ok.setOnClickListener(new View.OnClickListener(){ public void onClick(View _view){
							error = false;
							listItem = (HashMap<String, Object>)_view.getTag();
							if ((edit_thetext.getText().toString().length() > 0) && !error) {
								for (int k = 0; k < (int)(activeList.size()); k++) {
									if (activeList.get((int)k).get("text").toString().equals(edit_thetext.getText().toString()) && !activeList.get((int)k).get("text").toString().equals(listItem.get("text").toString())) {
										error = true;
										((EditText)edit_thetext).setError("This item already exists.");
									}
								}
								if (!error) {
									listItem.put("text", edit_thetext.getText().toString());
								}
							}
							else {
								error = true;
								((EditText)edit_thetext).setError("Please fill this field.");
							}
							if (listItem.containsKey("time") && !error) {
								listItem.put("time", String.valueOf((long)(Double.parseDouble(edit_time.getSelectedItem().toString().replaceAll("[^\\d]", "")))));
							}
							if (listItem.containsKey("reason") && !error) {
								listItem.put("reason", edit_reason.getText().toString());
							}
							if (listItem.containsKey("who") && !error) {
								listItem.put("who", edit_who.getText().toString());
							}
							if (!error) {
								_save_active_list();
								dialogMan.dismiss();
							}
							error = false;
						}
					});
					final Button btn_edit_cancel = (Button) bottomSheetView.findViewById(R.id.btn_edit_cancel);
					btn_edit_cancel.setOnClickListener(new View.OnClickListener(){ public void onClick(View _view){
							dialogMan.dismiss();
						}
					});
					dialogMan.setCancelable(true);
					dialogMan.show();
				}
			});
			
			return _view;
		}
	}
	
	@Deprecated
	public void showMessage(String _s) {
		Toast.makeText(getApplicationContext(), _s, Toast.LENGTH_SHORT).show();
	}
	
	@Deprecated
	public int getLocationX(View _v) {
		int _location[] = new int[2];
		_v.getLocationInWindow(_location);
		return _location[0];
	}
	
	@Deprecated
	public int getLocationY(View _v) {
		int _location[] = new int[2];
		_v.getLocationInWindow(_location);
		return _location[1];
	}
	
	@Deprecated
	public int getRandom(int _min, int _max) {
		Random random = new Random();
		return random.nextInt(_max - _min + 1) + _min;
	}
	
	@Deprecated
	public ArrayList<Double> getCheckedItemPositionsToArray(ListView _list) {
		ArrayList<Double> _result = new ArrayList<Double>();
		SparseBooleanArray _arr = _list.getCheckedItemPositions();
		for (int _iIdx = 0; _iIdx < _arr.size(); _iIdx++) {
			if (_arr.valueAt(_iIdx))
			_result.add((double)_arr.keyAt(_iIdx));
		}
		return _result;
	}
	
	@Deprecated
	public float getDip(int _input) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, _input, getResources().getDisplayMetrics());
	}
	
	@Deprecated
	public int getDisplayWidthPixels() {
		return getResources().getDisplayMetrics().widthPixels;
	}
	
	@Deprecated
	public int getDisplayHeightPixels() {
		return getResources().getDisplayMetrics().heightPixels;
	}
}