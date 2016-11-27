package ru.example.michael.saper;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;


//*** This Activity - is Description of Game. Activity build by HTML ***/
public class Description extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);
        TextView textDescription = (TextView) findViewById(R.id.textDescription);
        String infoText = getResources().getString(R.string.description);
        textDescription.setMovementMethod(new LinkMovementMethod());

        Spanned spannedText = Html.fromHtml(infoText, new ImageGetter(), null);
        Spannable reversedText = revertSpanned(spannedText);
        textDescription.setText(reversedText);
    }

    final Spannable revertSpanned(Spanned stext) {
        Object[] spans = stext.getSpans(0, stext.length(), Object.class);
        Spannable ret = Spannable.Factory.getInstance().newSpannable(stext.toString());
        if (spans != null && spans.length > 0) {
            for (int i = spans.length - 1; i >= 0; --i) {
                ret.setSpan(spans[i], stext.getSpanStart(spans[i]), stext.getSpanEnd(spans[i]), stext.getSpanFlags(spans[i]));
            }
        }
        return ret;
    }

    //*** Insert images to HTML ***
    private class ImageGetter implements Html.ImageGetter {
        public Drawable getDrawable(String source) {
            int id;
            switch (source) {
                case "btnmenu.png":
                    id = R.drawable.btnmenu;
                    break;
                case "menu_en.png":
                    id = R.drawable.menu_en;
                    break;
                case "menu_ru.png":
                    id = R.drawable.menu_ru;
                    break;
                case "single.png":
                    id = R.drawable.single;
                    break;
                case "first_step.png":
                    id = R.drawable.first_step;
                    break;
                case "chbmine_notchecked.png":
                    id = R.drawable.chbmine_notchecked;
                    break;
                case "chbmine_checked.png":
                    id = R.drawable.chbmine_checked;
                    break;
                case "second_step.png":
                    id = R.drawable.second_step;
                    break;
                case "finish.png":
                    id = R.drawable.finish;
                    break;
                case "bluetooth1_en.png":
                    id = R.drawable.bluetooth1_en;
                    break;
                case "bluetooth1_ru.png":
                    id = R.drawable.bluetooth1_ru;
                    break;
                case "btnbluemenu.png":
                    id = R.drawable.btnbluemenu;
                    break;
                case "bluetooth2_en.png":
                    id = R.drawable.bluetooth2_en;
                    break;
                case "bluetooth2_ru.png":
                    id = R.drawable.bluetooth2_ru;
                    break;
                case "bluetooth3_en.png":
                    id = R.drawable.bluetooth3_en;
                    break;
                case "bluetooth3_ru.png":
                    id = R.drawable.bluetooth3_ru;
                    break;
                case "bluetooth4_en.png":
                    id = R.drawable.bluetooth4_en;
                    break;
                case "bluetooth4_ru.png":
                    id = R.drawable.bluetooth4_ru;
                    break;
                case "btnsend.png":
                    id = R.drawable.btnsend;
                    break;
                default:
                    return null;
            }
            Drawable d = getResources().getDrawable(id);
            d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            return d;
        }
    }
}