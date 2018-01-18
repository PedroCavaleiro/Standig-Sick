package pt.ubi.pedrocavaleiro.standingsick;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Criado por pedrocavaleiro em 17/11/17.
 *
 * Bugs:
 *  Última verificação 22/11/2017 17:00 - 0 Encontrados
 *
 * Todo:
 *  Nada em falta
 */

public class DatabaseInterface extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "FORMULARIO.db" ;
    private static final int DATABASE_VERSION = 2;

    // Tabela e colunas para guardar as questões
    protected static final String QUESTIONTable = "Questao";
    protected static final String QTID = "Id";
    protected static final String QUESTION = "Questao";

    // Tabela e colunas para guardar as opções
    protected static final String ANSWERSTable = "Resposta";
    protected static final String ATID = "Id";
    protected static final String ANSWER = "Resposta";

    // Tabela e colunas para guardar a linkagem entre perguntas e respostas
    protected static final String LINKTable = "QALink";
    protected static final String QID = "QuestionID";
    protected static final String AID = "AnswerID";

    // Query para criar as tabelas
    // Tabela de questões, de respostas e de linkagem
    private static final String CREATE_QUESTION_TABLE = "CREATE TABLE IF NOT EXISTS " + QUESTIONTable + " (" +
            QTID + " INTEGER PRIMARY KEY, " +
            QUESTION + " VARCHAR(100));";
    private static final String CREATE_ANSWER_TABLE = "CREATE TABLE IF NOT EXISTS " + ANSWERSTable + " (" +
            ATID + " INTEGER PRIMARY KEY, " +
            ANSWER + " VARCHAR(50));";
    private static final String CREATE_LINK_TABLE = "CREATE TABLE IF NOT EXISTS " + LINKTable + " (" +
            QID + " INTEGER, " +
            AID + " INTEGER);";

    // Questões pré definidas
    private static final String CREATE_DEFAULT_QUESTION_FEVER = "INSERT INTO " + QUESTIONTable + " (" +
            QUESTION + ") VALUES ('Tem Febre?')";
    private static final String CREATE_DEFAULT_QUESTION_HEADACHE = "INSERT INTO " + QUESTIONTable + " (" +
            QUESTION + ") VALUES ('Tem dores de cabeça?')";
    private static final String CREATE_DEFAULT_QUESTION_ABDOMEN = "INSERT INTO " + QUESTIONTable + " (" +
            QUESTION + ") VALUES ('Tem dores no abdômen?')";
    private static final String CREATE_DEFAULT_QUESTION_ARMLEG = "INSERT INTO " + QUESTIONTable + " (" +
            QUESTION + ") VALUES ('Tem dores nos braços ou nas pernas?')";

    // Resposta pré definidas
    private static final String CREATE_DEFAULT_ANSWER_YES = "INSERT INTO " + ANSWERSTable + " (" +
            ANSWER + ") VALUES ('Sim')";
    private static final String CREATE_DEFAULT_ANSWER_NO = "INSERT INTO " + ANSWERSTable + " (" +
            ANSWER + ") VALUES ('Não')";


    SQLiteDatabase oSQLiteDB;

    public DatabaseInterface(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        oSQLiteDB = this.getWritableDatabase();
    }

    /**
     * Reabrir a base de dados
     */
    public void reopenDB() {
        oSQLiteDB = this.getReadableDatabase();
    }

    /**
     * Fechar a base de dados
     */
    public void closeDB() {
        oSQLiteDB.close();
    }

    /**
     * Conta o nº de questões guardadas na base de dados
     * @return inteiro com nº de questões na base de dados
     */
    public int countQuestions() {
        String query = "SELECT * FROM " + QUESTIONTable;
        Cursor res = oSQLiteDB.rawQuery(query, null);
        return res.getCount();
    }

    /**
     * Obtem todas as questões guardadas na base de dados
     * @return lista de objetos do tipo Question com todas as questões na base de dados
     */
    public List<Question> getQuestions() {
        List<Question> questionsList = new ArrayList<Question>();
        String query = "SELECT * FROM " + QUESTIONTable;
        Cursor res = oSQLiteDB.rawQuery(query, null);
        while (res.moveToNext()) {
            Question q = new Question(
                    res.getInt(0),
                    res.getString(1)
            );
            questionsList.add(q);
        }
        return questionsList;
    }

    /**
     * Obtem todas as respostas guardadas na base de dados
     * @return ArrayList com elementos do tipo Answer
     */
    public ArrayList<Answer> getAnswers() {
        ArrayList<Answer> answerList = new ArrayList<Answer>();
        String query = "SELECT * FROM " + ANSWERSTable;
        Cursor res = oSQLiteDB.rawQuery(query, null);
        while (res.moveToNext()) {
            Answer a = new Answer(
                    res.getInt(0),
                    res.getString(1)
            );
            answerList.add(a);
        }
        return answerList;
    }

    /**
     * Edita o texto de uma resposta
     * @param answerID id da resposta
     */
    public void editAnswer(int answerID, String newText) {
        String query = "UPDATE " + ANSWERSTable + " SET " + ANSWER + "='" + newText +
                "' WHERE " + ATID + "='" + answerID + "';";
        oSQLiteDB.execSQL(query);
    }

    /**
     * Adiciona uma nova resposta
     * @param newText texto da nova resposta
     */
    public void addAnswer(String newText) {
        String query = "INSERT INTO " + ANSWERSTable + " (" + ANSWER + ") VALUES ('" + newText + "')";
        oSQLiteDB.execSQL(query);
    }

    /**
     * Verifica se a resposta já se encontra atribuida à pergunta
     * @param questionID id da pergunta a verificar
     * @param answerID id da resposta
     * @return boolean
     */
    public boolean isLinked(int questionID, int answerID) {
        String query = "SELECT * FROM " + LINKTable + " WHERE " + AID + "='" + answerID +
                "' AND " + QID + "='" + questionID + "';";
        Cursor res = oSQLiteDB.rawQuery(query, null);

        if(!res.moveToFirst())
            return false;
        else
            return true;
    }

    /**
     * Verifica se uma dada resposta já existe na base de dados
     * @param answer resposta a verificar
     * @return boolean
     */
    public boolean answerExsists(String answer) {
        String query = "SELECT " + ANSWER + " FROM " + ANSWERSTable + " WHERE " + ANSWER +
                "='" + answer + "';";
        Cursor res = oSQLiteDB.rawQuery(query, null);

        if(res.moveToFirst())
            return true;
        else
            return false;
    }

    /**
     * Adiciona uma opção a uma pergunta
     * @param questionID id da questão
     * @param answerID id da resposta
     */
    public void linkAnswer(int questionID, int answerID) {
        String query = "INSERT INTO " + LINKTable + " (" + QID + ", " + AID + ") VALUES ('" +
                questionID + "', '" + answerID + "');";
        oSQLiteDB.execSQL(query);
     }

    /**
     * Obtem uma questão especifica
     * @param questionID o id da questão a carregar
     * @return a questão escolhida como tipo Question
     */
    public Question getQuestion(int questionID) {
        String query = "SELECT * FROM " + QUESTIONTable + " WHERE " + QTID + "='" + questionID + "';";
        Question q = null;
        Cursor questionCursor = oSQLiteDB.rawQuery(query, null);
        if (questionCursor != null) {
            questionCursor.moveToFirst();
            q = new Question(questionCursor.getInt(0), questionCursor.getString(1));
        }
        return q;
    }

    /**
     * Obtem as opções para uma questão
     * @param questionID id da questão (inteiro)
     * @return retorna uma lista com as opções
     */
    public List<String> getOptions(int questionID) {
        List<String> optionsList = new ArrayList<String>();
        String linkQuery = "SELECT " + AID + " FROM " + LINKTable + " WHERE " + QID + "='" + questionID + "';";
        Cursor linkCursor = oSQLiteDB.rawQuery(linkQuery, null);
        while (linkCursor.moveToNext()) {
            String answerQuery = "SELECT " + ANSWER + " FROM " + ANSWERSTable + " WHERE " + ATID +
                    "='" + linkCursor.getString(0) + "';";
            Cursor answerCursor = oSQLiteDB.rawQuery(answerQuery, null);
            answerCursor.moveToNext();
            optionsList.add(answerCursor.getString(0));
        }
        return optionsList;
    }

    /**
     * Obtem as opções para uma questão como um ArrayList de Answer
     * @param questionID id da questão
     * @return opções
     */
    public ArrayList<Answer> getOptionsAsAnswer(int questionID) {
        ArrayList<Answer> optionsList = new ArrayList<Answer>();
        String linkQuery = "SELECT " + AID + " FROM " + LINKTable + " WHERE " + QID + "='" + questionID + "';";
        Cursor linkCursor = oSQLiteDB.rawQuery(linkQuery, null);
        while (linkCursor.moveToNext()) {
            String answerQuery = "SELECT * FROM " + ANSWERSTable + " WHERE " + ATID +
                    "='" + linkCursor.getString(0) + "';";
            Cursor answerCursor = oSQLiteDB.rawQuery(answerQuery, null);
            answerCursor.moveToFirst();
            Answer ans = new Answer(answerCursor.getInt(0), answerCursor.getString(1));
            optionsList.add(ans);
        }
        return optionsList;
    }

    /**
     * Remove uma opção de uma dada questão, esta ligação encontra-se na tabela "QALink"
     * @param questionID o id da questão
     * @param answerID o id da resposta
     */
    public void deleteOption(int questionID, int answerID) {
        String linkQuery = "DELETE FROM " + LINKTable + " WHERE " + QID + "='" + questionID +
                "' AND " + AID + "='" + answerID + "';";
        oSQLiteDB.execSQL(linkQuery);
    }

    /**
     * Remove uma resposta, removendo também a mesma de todas as perguntas onde se encontra associada
     * @param answerID id da resposta a ser removida
     */
    public void deleteAnswer(int answerID) {
        String removeLinksQuery = "DELETE FROM " + LINKTable + " WHERE " + AID + "='" + answerID + "';";
        oSQLiteDB.execSQL(removeLinksQuery);
        String deleteAnswer = "DELETE FROM " + ANSWERSTable + " WHERE " + ATID + "='" + answerID + "';";
        oSQLiteDB.execSQL(deleteAnswer);
    }

    /**
     * Remove uma questão
     * @param questionID id da questão
     */
    public void deleteQuestion(int questionID) {
        String removeLinking = "DELETE FROM " + LINKTable + " WHERE " + QID + "='" + questionID + "';";
        oSQLiteDB.execSQL(removeLinking);
        String deleteQuestion = "DELETE FROM " + QUESTIONTable + " WHERE " + QTID + "='" + questionID + "';";
        oSQLiteDB.execSQL(deleteQuestion);
    }

    /**
     * Verifica se a questão já existe na base de dados
     * @param newQuestion nova questão
     */
    public boolean questionExists(String newQuestion) {
        String query = "SELECT " + QUESTION + " FROM " + QUESTIONTable + " WHERE " + QUESTION +
                "='" + newQuestion + "';";
        Cursor res = oSQLiteDB.rawQuery(query, null);

        if(res.moveToFirst())
            return true;
        else
            return false;
    }

    /**
     * Adiciona uma nova questão à base de dados devolvendo o ID da nova questão
     * @param newQuestion texto da nova questão
     * @return id da questão introduzida
     */
    public int addQuestion(String newQuestion) {
        String insert = "INSERT INTO " + QUESTIONTable + " (" + QUESTION + ") VALUES ('" +
                newQuestion + "');";
        oSQLiteDB.execSQL(insert);
        String lastID = "SELECT " + QTID + " FROM " + QUESTIONTable + " ORDER BY " + QTID + " DESC LIMIT 1";
        Cursor getLastID = oSQLiteDB.rawQuery(lastID, null);
        getLastID.moveToNext();
        return getLastID.getInt(0);
    }

    /**
     * Atualiza a questão com o novo texto
     * @param questionID id da questão
     * @param newText novo texto
     */
    public void editQuestion(int questionID, String newText) {
        String query = "UPDATE " + QUESTIONTable + " SET " + QUESTION + "='" + newText +
                "' WHERE " + QTID + "='" + questionID + "';";
        oSQLiteDB.execSQL(query);
    }

    /**
     * Repomos a base de dados ao seu estado original
     */
    public void resetDatabase() {
        String dropQuestions = "DROP TABLE " + QUESTIONTable;
        String dropAnswers = "DROP TABLE " + ANSWERSTable;
        String dropLinks = "DROP TABLE " + LINKTable;
        oSQLiteDB.execSQL(dropQuestions);
        oSQLiteDB.execSQL(dropAnswers);
        oSQLiteDB.execSQL(dropLinks);
        oSQLiteDB.execSQL(CREATE_QUESTION_TABLE);
        oSQLiteDB.execSQL(CREATE_ANSWER_TABLE);
        oSQLiteDB.execSQL(CREATE_LINK_TABLE);
        oSQLiteDB.execSQL(CREATE_DEFAULT_QUESTION_FEVER);
        oSQLiteDB.execSQL(CREATE_DEFAULT_QUESTION_HEADACHE);
        oSQLiteDB.execSQL(CREATE_DEFAULT_QUESTION_ABDOMEN);
        oSQLiteDB.execSQL(CREATE_DEFAULT_QUESTION_ARMLEG);
        oSQLiteDB.execSQL(CREATE_DEFAULT_ANSWER_YES);
        oSQLiteDB.execSQL(CREATE_DEFAULT_ANSWER_NO);

        // Geramos os links entre as questões e as respostas
        for (int  i = 1; i <= 4; i++) {
            String insertYES = "INSERT INTO " + LINKTable + " (" + QID + ", " + AID + ") VALUES ('" + i + "', '1')";
            String insertNO = "INSERT INTO " + LINKTable + " (" + QID + ", " + AID + ") VALUES ('" + i + "', '2')";
            oSQLiteDB.execSQL(insertYES);
            oSQLiteDB.execSQL(insertNO);
        }
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_QUESTION_TABLE);
        sqLiteDatabase.execSQL(CREATE_ANSWER_TABLE);
        sqLiteDatabase.execSQL(CREATE_LINK_TABLE);
        sqLiteDatabase.execSQL(CREATE_DEFAULT_QUESTION_FEVER);
        sqLiteDatabase.execSQL(CREATE_DEFAULT_QUESTION_HEADACHE);
        sqLiteDatabase.execSQL(CREATE_DEFAULT_QUESTION_ABDOMEN);
        sqLiteDatabase.execSQL(CREATE_DEFAULT_QUESTION_ARMLEG);
        sqLiteDatabase.execSQL(CREATE_DEFAULT_ANSWER_YES);
        sqLiteDatabase.execSQL(CREATE_DEFAULT_ANSWER_NO);

        // Geramos os links entre as questões e as respostas
        for (int  i = 1; i <= 4; i++) {
            String insertYES = "INSERT INTO " + LINKTable + " (" + QID + ", " + AID + ") VALUES ('" + i + "', '1')";
            String insertNO = "INSERT INTO " + LINKTable + " (" + QID + ", " + AID + ") VALUES ('" + i + "', '2')";
            sqLiteDatabase.execSQL(insertYES);
            sqLiteDatabase.execSQL(insertNO);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // Sem upgrade necessário
    }
}
