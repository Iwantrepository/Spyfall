package com.example.spyfall;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class infoBar extends AppCompatActivity {


    String path;
    String pathFromToUpload;
    String logString;

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
}