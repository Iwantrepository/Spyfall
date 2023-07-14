package com.example.spyfall;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;

public class infoBar extends AppCompatActivity {


    int hour, minute;
    String path;
    String pathFromToUpload;
    String logString;

    CountDownTimer timer;

    SoundPool soundPool;
    int sound;
    boolean isInTimer = false;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_bar);

        Bundle arguments = getIntent().getExtras();

        if(arguments!=null){
            path = (String) arguments.get("path");
            pathFromToUpload = (String) arguments.get("pathFromToUpload");

            logString = (String) arguments.get("logString");

        }

        TextView textView_info = (TextView) findViewById(R.id.textView_info);
        textView_info.setText(
                "Version: " + BuildConfig.VERSION_NAME+"\n"
                +"▼ LOG ▼"+ "\n" + logString+"\n"
        );


        AudioAttributes audioAttributes = new AudioAttributes.Builder()
//                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_GAME)
//                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(1)
                .setAudioAttributes(audioAttributes)
                .build();

        sound = soundPool.load(this, R.raw.joke, 1);
    }

    boolean writeFile(String filename, String input) {
        File fileName = new File(filename);

        if (!fileName.exists()) {
            try {
                fileName.createNewFile();
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "Файл не может быть создан" , Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                return false;
            }
        }

        try {
            FileWriter f = new FileWriter(fileName);
            f.write(input);
            f.flush();
            f.close();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Ошибка записи" , Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void testLocks(View view) {



        Toast.makeText(getApplicationContext(), "Добавлены наборы тестовых локаций ", Toast.LENGTH_LONG).show();


        writeFile(path+"/pack.txt", "1\n2\n3\n4\n5\n6\n7");
        writeFile(path+"/pack1.txt",   "pack\n1\n2\n3\n4\n5\n6\n7");
        writeFile(path+"/pack2.txt",   "pack\n1\n2\n3\n4\n5\n6\n7");
        writeFile(path+"/pack3.txt",   "pack\n1\n2\n3\n4\n5\n6\n7");
        writeFile(path+"/pack4.txt",   "pack\n1\n2\n3\n4\n5\n6\n7");
        writeFile(path+"/pack5.txt",   "pack\n1\n2\n3\n4\n5\n6\n7");

        writeFile(path+"/aralen.txt",  "pack\n1\n2\n3\n4\n5\n6\n7");
        writeFile(path+"/aralen1.txt", "pack\n1\n2\n3\n4\n5\n6\n7");
        writeFile(path+"/aralen2.txt", "pack\n1\n2\n3\n4\n5\n6\n7");
        writeFile(path+"/aralen3.txt", "pack\n1\n2\n3\n4\n5\n6\n7");
        writeFile(path+"/aralen4.txt", "pack\n1\n2\n3\n4\n5\n6\n7");
        writeFile(path+"/aralen5.txt", "pack\n1\n2\n3\n4\n5\n6\n7");

        writeFile(path+"/ratong.txt",  "pack\n1\n2\n3\n4\n5\n6\n7");
        writeFile(path+"/ratong1.txt", "pack\n1\n2\n3\n4\n5\n6\n7");
        writeFile(path+"/ratong2.txt", "pack\n1\n2\n3\n4\n5\n6\n7");
        writeFile(path+"/ratong3.txt", "pack\n1\n2\n3\n4\n5\n6\n7");
        writeFile(path+"/ratong4.txt", "pack\n1\n2\n3\n4\n5\n6\n7");
        writeFile(path+"/ratong5.txt", "pack\n1\n2\n3\n4\n5\n6\n7");
    }

    public void SpyFirstLocs(View view) {

        Toast.makeText(getApplicationContext(), "Добавлен набор первого издания", Toast.LENGTH_LONG).show();

        writeFile(path+"/Spyfall_First1.txt", "Ресторан\nМузыкант\nПосетитель\nВышибала\nМетрдотель\nШеф-Повар\nОфициант\nКритик");
        writeFile(path+"/Spyfall_First2.txt", "Спа-Салон\nКлиент\nСтилист\nМассажист\nВизажист\nМаникюрщик\nКосметолог\nДерматолог");
        writeFile(path+"/Spyfall_First3.txt", "Отель\nБармен\nПостоялец\nГорничная\nШвейцар\nОхранник\nУправляющий\nПортье");
        writeFile(path+"/Spyfall_First4.txt", "Университет\nСтудент\nВахтёр\nРектор\nПрофессор\nАспирант\nПсихолог\nДекан");
        writeFile(path+"/Spyfall_First5.txt", "Банк\nКлиент\nОхранник\nГрабитель\nКассир\nУправляющий\nИнкассатор\nКонсультант");
        writeFile(path+"/Spyfall_First6.txt", "Больница\nТерапевт\nХирург\nМедсестра\nПациент\nПатологоанатом\nГлавврач\nИнтерн");
        writeFile(path+"/Spyfall_First7.txt", "Посольство\nДипломат\nПосол\nЧиновник\nСекретарь\nОхранник\nТурист\nБеженец");
        writeFile(path+"/Spyfall_First8.txt", "Киностудия\nЗвукооператор\nКаскадёр\nОператор\nКостюмер\nРежиссёр\nАктёр\nСтатист");
        writeFile(path+"/Spyfall_First9.txt", "Цирк-шапито\nДрессировщик\nПосетитель\nФокусник\nЖонглёр\nКлоун\nМетатель ножей\nАкробат");
        writeFile(path+"/Spyfall_First10.txt", "Театр\nГардеробщик\nСуфлёр\nБилетёр\nЗритель\nРежиссёр\nАктёр\nРабочий сцены");
        writeFile(path+"/Spyfall_First11.txt", "Церковь\nСвященник\nГрешник\nХорист\nПрихожанин\nНищий\nТурист\nМеценат");
        writeFile(path+"/Spyfall_First12.txt", "Овощебаза\nСанинспектор\nОхранник\nГрузчик\nБухгалтер\nБригадир\nЛаборант\nВодитель");
        writeFile(path+"/Spyfall_First13.txt", "Супермаркет\nМерчендайзер\nОхранник\nПромоутер\nКассир\nМясник\nУборщик\nПокупатель");
        writeFile(path+"/Spyfall_First14.txt", "Полицейский участок\nЖурналист\nДетектив\nАдвокат\nКриминалист\nАрхивариус\nПатрульный\nПреступник");
        writeFile(path+"/Spyfall_First15.txt", "Корпоративная Вечеринка\nМенеджер\nВедущий\nНезваный гость\nШеф\nСекретарь\nБухгалтер\nКурьер");
        writeFile(path+"/Spyfall_First16.txt", "Океанский лайнер\nБогатый пассажир\nСтюард\nКапитан\nБармен\nМузыкант\nКок\nРадист");
        writeFile(path+"/Spyfall_First17.txt", "Подводная лодка\nЭлектромеханик\nМатрос\nКомандир\nШтурман\nГидроакустик\nКок\nРадист");
        writeFile(path+"/Spyfall_First18.txt", "Станция техобслуживания\nМастер-приёмщик\nШиномонтажник\nМойщик\nМотоциклист\nЭлектрик\nДиректор\nАвтомобилист");
        writeFile(path+"/Spyfall_First19.txt", "Полярная станция\nНачальник экспедиции\nГеофизик\nБиолог\nМетеоролог\nМедик\nГидролог\nРадист");
        writeFile(path+"/Spyfall_First20.txt", "Пляж\nФотограф с обезьянкой\nАниматор\nОтдыхающий\nПарапланерист\nРазносчик еды\nСпасатель\nВор");
        writeFile(path+"/Spyfall_First21.txt", "Воинская часть\nПродавец в магазине\nМладший офицер\nВрач\nКараульный\nРядовой\nПолковник\nДезертир");
        writeFile(path+"/Spyfall_First22.txt", "Войско крестоносцев\nПлененный сарацин\nСлуга\nЕпископ\nЛучник\nМонах\nОруженосец\nРыцарь");
        writeFile(path+"/Spyfall_First23.txt", "Пассажирский поезд\nРазносчик товаров\nПовар вагона-ресторана\nПассажир\nПограничник\nПроводник\nМашинист\nКочегар");
        writeFile(path+"/Spyfall_First24.txt", "Школа\nКлассный руководитель\nОхранник\nФизрук\nШкольник\nДиректор\nУборщик\nЗавуч");
        writeFile(path+"/Spyfall_First25.txt", "База террористов\nМальчик на побегушках\nГлава ячейки\nБоевик\nСмертник\nМедик\nНовобранец\nПастух");
        writeFile(path+"/Spyfall_First26.txt", "Партизанский отряд\nПленный полицай\nРазведчик\nПартизан\nРадист\nМедик\nСолдат\nПовар");
        writeFile(path+"/Spyfall_First27.txt", "Казино\nАзартный посетитель\nНачальник охраны\nВышибала\nШулер\nБармен\nАдминистратор\nКрупье");
        writeFile(path+"/Spyfall_First28.txt", "Пиратский корабль\nСвязанный пленник\nКанонир\nМатрос\nЮнга\nКапитан\nКок\nРаб");
        writeFile(path+"/Spyfall_First29.txt", "Орбитальная станция\nКосмический турист\nИнженер-исследователь\nБезумный ИИ\nВрач\nПилот\nКомандир\nИнопланетянин");
        writeFile(path+"/Spyfall_First30.txt", "Самолёт\nКомандир экипажа\nПассажир бизнес-класса\nПассажир эконом-класса\nСтюард\nВторой пилот\nБортинженер\nБезбилетник");
    }

    public void buttonOldSixLoc(View view) {
        Toast.makeText(getApplicationContext(), "Добавлено немножко стартовых локаций ", Toast.LENGTH_LONG).show();
        writeFile(path+"/starter_pack_1.txt", "Университет\nПрогульщик\nПодлиза\nОтличник\nПофигист\nИдущий на красный диплом\nПреподаватель\nПостоянно ест");
        writeFile(path+"/starter_pack_2.txt", "Школа\nКурит за школой\nСтароста\nХулиган\nОхранник\nУставший учитель\nДиректор\nОтличник");
        writeFile(path+"/starter_pack_3.txt", "База террористов\nОбожает телеграмм\nКоординатор\nРазведчик\nЛюбитель ножей\nОтвечает за боезапас\nОтветственный за шифры\nБезответственный создатель бомб");
        writeFile(path+"/starter_pack_4.txt", "Форум в интернете\nНедовольный комментатор\nМодератор\nЗадает глупые вопросы\nРугается с модератором\nПытается красиво оформить топик\nШибко умный участник форума\nТот, кто начал этот тред");
        writeFile(path+"/starter_pack_5.txt", "Рок-концерт\nСтоит у самой колонки\nПытается прыгнуть в толпу\nПодпевает (кричит)\nГлавный голос сцены\nБэквокалист\nЗабытый за кулисами клавишник\nПьяный фанат");
        writeFile(path+"/starter_pack_6.txt", "Церквушка окутанная коррупцией\nПоп\nНедовольный прихожанин\nТорговец свечками\nПлакальщица\nНелегально продает свечки\nОдержимая религией мать\nРебенок одержимой матери");
    }

    public void playSound(View view) {

        soundPool.play(sound, 1, 1, 0, 0, 1);

//        if(isInTimer){
//            timer.cancel();
//            timer.onFinish();
//            isInTimer = false;
//            soundPool.autoPause();
//        }else {
//            isInTimer = true;
//
//            soundPool.play(sound, 1, 1, 0, -1, 1);
//            timer = new CountDownTimer(5000, 1000) {
//
//                Button button = (Button) findViewById(R.id.buttonSound);
//
//                public void onTick(long millisUntilFinished) {
//                    button.setText("Осталось: "
//                            + millisUntilFinished / 1000);
//
//                }
//
//                public void onFinish() {
//                    button.setText("SOUND");
//                    isInTimer = false;
//                    soundPool.stop(sound);
//
//
//                }
//            };
//            timer.start();
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        soundPool.release();
        soundPool = null;
    }

    public void imageButtonCustomTime(View view) {



        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener()
        {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute)
            {
                hour = selectedHour;
                minute = selectedMinute;
                Toast.makeText(getApplicationContext(), String.format(Locale.getDefault(), "%02d:%02d",hour, minute), Toast.LENGTH_LONG).show();

                SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preferenceFileKey),MODE_MULTI_PROCESS);
                long millis = hour*60000 + minute*1000;
                sharedPreferences.edit().putLong("timeSP2",millis).apply();
            }
        };

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, /*style,*/ onTimeSetListener, hour, minute, true);
        timePickerDialog.setTitle("Select Time");
        timePickerDialog.show();
    }

    public void imageButtonClearSharedPrefs(View view) {

        AlertDialog.Builder alert = new AlertDialog.Builder(infoBar.this, R.style.MyDialogTheme);
        alert.setTitle("Внимание");
        alert.setMessage("Вы действительно хотите удалить внутренние настройки?");
        alert.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //
            }
        });
        alert.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                getSharedPreferences(getString(R.string.preferenceFileKey),MODE_PRIVATE).edit().clear().apply();
                Toast.makeText(getApplicationContext(), "Сохранение состояния обнулено", Toast.LENGTH_LONG).show();
            }
        });
        alert.create();
        alert.show();
    }

    public void imageButtonAlphanumeric(View view) {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preferenceFileKey),MODE_MULTI_PROCESS);
        if(sharedPreferences.getBoolean("alphanumericSort", false)){
            sharedPreferences.edit().putBoolean("alphanumericSort", false).apply();
            Toast.makeText(getApplicationContext(), "Alphanumeric OFF", Toast.LENGTH_SHORT).show();
        }else{
            sharedPreferences.edit().putBoolean("alphanumericSort", true).apply();
            Toast.makeText(getApplicationContext(), "Alphanumeric ON", Toast.LENGTH_SHORT).show();
        }
    }

    public void SpyThirdLocs(View view) {
        Toast.makeText(getApplicationContext(), "Добавлен набор третьего издания", Toast.LENGTH_LONG).show();

        writeFile(path+"/Spyfall_Third1.txt", "Караван\nТорговец специями\nОхранник\nПопрошайка\nПогонщик\nКупец\nКараванщик\nРазбойник");
        writeFile(path+"/Spyfall_Third2.txt", "Древнеримский сенат\nИмператор\nКонсул\nСенатор\nСлуга\nПлебей\nЦензор\nПатриций");
        writeFile(path+"/Spyfall_Third3.txt", "Круг друидов\nДруид\nБард\nИсцеленный кельт\nРимский лазутчик\nУченик друида\nПредсказатель\nОтшельник");
        writeFile(path+"/Spyfall_Third4.txt", "Древняя олимпиада\nДискобол\nБегун\nБорец\nМетатель копья\nСудья\nПрыгун\nЗритель");
        writeFile(path+"/Spyfall_Third5.txt", "Троянская война\nАхилл\nПарис\nОдиссей\nГектор\nЕлена Прекрасная\nГомер\nКассандра");
        writeFile(path+"/Spyfall_Third6.txt", "Строительство пирамид\nФараон\nАрхитектор\nЖрец\nКаменотёс\nНадсмотрщик\nПисец\nРабочий");
        writeFile(path+"/Spyfall_Third7.txt", "Пещера неандертальцев\nВождь\nШаман\nХудожник\nСтарик\nСвязанный людоед\nРебенок\nОхотник");
        writeFile(path+"/Spyfall_Third8.txt", "Лагерь повстанцев\nКоманданте\nЖурналист\nЗаключенный\nПовстанец\nПровокатор\nАгент ЦРУ\nПолковник КГБ");
        writeFile(path+"/Spyfall_Third9.txt", "Танковый бой\nКомандир танка\nВодитель танка\nНаводчик танка\nРадист\nПулеметчик\nРазведчик\nСапёр");
        writeFile(path+"/Spyfall_Third10.txt", "Подпольный бар\nВышибала\nБармен\nМафиози\nПьяница\nКартёжник\nБутлегер\nПолицейский");
        writeFile(path+"/Spyfall_Third11.txt", "Дирижабль первой мировой\nКапитан\nРадист\nМетеоролог\nСтрелок\nМеханик\nШтурман\nРадиотелеграфист");
        writeFile(path+"/Spyfall_Third12.txt", "Лагерь золотоискателей\nРепортер\nСтаратель\nПолицейский\nСкупщик\nИндеец\nВрач\nМошенник");
        writeFile(path+"/Spyfall_Third13.txt", "Салун\nБармен\nОхотник за головами\nБандит\nШериф\nШулер\nЗолотоискатель\nГробовщик");
        writeFile(path+"/Spyfall_Third14.txt", "Французская революция\nСолдат\nРеволюционер\nСтудент\nБеспризорник\nРабочий\nЭмигрант\nБуржуа");
        writeFile(path+"/Spyfall_Third15.txt", "Воздушный шар\nГеограф-англичанин\nМиссионер-итальянец\nЭнтомолог-немец\nБиолог-русский\nПутешественник-американец\nПроводник-эфиоп\nПереводчик-француз");
        writeFile(path+"/Spyfall_Third16.txt", "Бородинская битва\nНаполеон\nКутузов\nКавалерист\nАртиллерист\nГусар\nПоручик Ржевский\nДворянин");
        writeFile(path+"/Spyfall_Third17.txt", "Вигвам\nШаман\nВождь\nРебенок\nПленённый ковбой\nЦелитель\nБледнолицый\nСтарик");
        writeFile(path+"/Spyfall_Third18.txt", "Рота мушкетёров\nКардинал Решельё\nД'Артаньян\nАтос\nПортос\nАрамис\nДе Тревиль\nМиледи");
        writeFile(path+"/Spyfall_Third19.txt", "Школа ниндзя\nНаставник\nНовичок\nРонин\nКузнец\nМонах\nПосланник сёгуна\nСлуга");
        writeFile(path+"/Spyfall_Third20.txt", "Турецкий гарем\nСултан\nНаложница\nЕвнух\nМать султана\nСтражник\nВизирь\nЛекарь");
        writeFile(path+"/Spyfall_Third21.txt", "Испанская инквизиция\nПалач\nСвященник\nСудья\nПленник\nТюремщик\nСекретарь\nЧиновник");
        writeFile(path+"/Spyfall_Third22.txt", "Остров людоедов\nВождь\nЖрец\nПленённый англичанин\nВоин\nПовар\nРебёнок\nОхотник");
        writeFile(path+"/Spyfall_Third23.txt", "Мастерская Леонардо\nЛеонардо\nЗаказчик\nПосланник папы\nПодмастерье\nСлуга\nФлорентийский ученый\nБогослов");
        writeFile(path+"/Spyfall_Third24.txt", "Эпидемия Чёрной смерти\nЧумной доктор\nСмертельно больной\nСвященник\nАптекарь\nМогильщик\nИзвозчик\nСолдат");
        writeFile(path+"/Spyfall_Third25.txt", "Монастырь Шаолинь\nНастоятель\nПовар\nКаллиграф\nВеликий мастер ушу\nЮный монах\nЕвропейский гость\nЧиновник");
        writeFile(path+"/Spyfall_Third26.txt", "Рыцарский турнир\nПрекрасная дама\nТорговец\nРыцарь\nДворянин\nПростолюдин\nПаж\nСвященник");
        writeFile(path+"/Spyfall_Third27.txt", "Отряд Робин Гуда\nРобин Гуд\nБрат Тук\nМаленький Джон\nДева Мариан\nШериф Ноттингемский\nМенестрель\nУилл Скарлет");
        writeFile(path+"/Spyfall_Third28.txt", "Ладья Викингов\nГребец\nВоин\nВоевода\nЯрл\nЦенный пленник\nСкальд\nБерсерк");
        writeFile(path+"/Spyfall_Third29.txt", "Лунная станция\nИнопланетянин\nКомандир\nБиолог\nУборщица\nИскуственный интеллект\nПсихолог\nКосмический турист");
        writeFile(path+"/Spyfall_Third30.txt", "Фестиваль хиппи\nСолист\nГитарист\nХиппи\nВозмущенный фермер\nЖурналист\nМестный житель\nНаркодилер");
    }
}