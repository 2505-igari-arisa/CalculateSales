package jp.alhinc.calculate_sales;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalculateSales {

	// 支店定義ファイル名
	private static final String FILE_NAME_BRANCH_LST = "branch.lst";

	// 支店別集計ファイル名
	private static final String FILE_NAME_BRANCH_OUT = "branch.out";

	// エラーメッセージ
	private static final String UNKNOWN_ERROR = "予期せぬエラーが発生しました";
	private static final String FILE_NOT_EXIST = "支店定義ファイルが存在しません";
	private static final String FILE_INVALID_FORMAT = "支店定義ファイルのフォーマットが不正です";

	/**
	 * メインメソッド
	 *
	 * @param コマンドライン引数
	 */
	public static void main(String[] args) {
		// 支店コードと支店名を保持するMap
		Map<String, String> branchNames = new HashMap<>();
		// 支店コードと売上金額を保持するMap
		Map<String, Long> branchSales = new HashMap<>();

		// 支店定義ファイル読み込み処理
		if(!readFile(args[0], FILE_NAME_BRANCH_LST, branchNames, branchSales)) {
			return;
		}

		// ※ここから集計処理を作成してください。(処理内容2-1、2-2)
		//全てのファイルを取得する
		File[] files = new File (args[0]).listFiles();

		//条件に一致したファイルだけを入れるリスト
		List<File> rcdFiles = new ArrayList<>();

		//取得したファイルに格納されているファイル名を取得
		for(int i = 0; i < files.length ; i++) {

			//ファイル名の条件が一致しているか（0-9の8桁で始まって.rcdで終わる）
			if(files[i].getName().matches("^[0-9]{8}[.]rcd$")) {

				//trueならrcdFilesリストに入れる※falsなら入らない
				rcdFiles.add(files[i]);
			}
		}
		//ここから処理内容2-2
		//リストに入ったファイルの数だけ繰り返し処理
		for(int i = 0; i < rcdFiles.size(); i++) {

			//2-1で選別したファイルから読み込んだ支店コードと売上額を格納するリスト(文字列)
			List<String> rcdCodeFiles = new ArrayList<>();

			//2-1でaddしたファイルを読み込む
			BufferedReader rcdbr = null;

				try {
					//rcdFileから中身を確認するファイルを取り出して、ファイル名を取得する
					File openfile = new File(args[0],rcdFiles.get(i).getName());
					FileReader rcdfr = new FileReader(openfile);
					rcdbr = new BufferedReader(rcdfr);

					// 売上ファイルを一行ずつ読み込む
					String rcdline;
					while((rcdline = rcdbr.readLine()) != null) {

					//一行ごとに内容を格納するリストの中に入れる※rcdCodeFilesリストに入れる
					rcdCodeFiles.add(rcdline);

					//支店コードと売上金額を保持するMapと同じ<String, Long>にする
					long fileSale = Long.parseLong(rcdline);

					//Map(HashMap)から値を取得する
					Long saleAmount = branchSales.get + fileSale;

					}

		// 支店別集計ファイル書き込み処理
		if(!writeFile(args[0], FILE_NAME_BRANCH_OUT, branchNames, branchSales)) {
			return;
		}
		}
		}
	}

	/**
	 * 支店定義ファイル読み込み処理
	 *
	 * @param フォルダパス
	 * @param ファイル名
	 * @param 支店コードと支店名を保持するMap
	 * @param 支店コードと売上金額を保持するMap
	 * @return 読み込み可否
	 */
	private static boolean readFile(String path, String fileName, Map<String, String> branchNames, Map<String, Long> branchSales) {
		BufferedReader br = null;

		try {
			File file = new File(path, fileName);
			FileReader fr = new FileReader(file);
			br = new BufferedReader(fr);

			String line;
			// 支店定義ファイルを一行ずつ読み込む
			while((line = br.readLine()) != null) {
				// ※ここの読み込み処理を変更してください。(処理内容1-2)
				//カンマの位置で分割
				String[] items = line.split(",");

				//支店コードと支店名を保持するMapに追加する2つの情報をputの引数として指定
			    branchNames.put(items[0],items[1]);
			    branchSales.put("支店コード", 0L);

				System.out.println(line);
			}

		} catch(IOException e) {
			System.out.println(UNKNOWN_ERROR);
			return false;
		} finally {
			// ファイルを開いている場合
			if(br != null) {
				try {
					// ファイルを閉じる
					br.close();
				} catch(IOException e) {
					System.out.println(UNKNOWN_ERROR);
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * 支店別集計ファイル書き込み処理
	 *
	 * @param フォルダパス
	 * @param ファイル名
	 * @param 支店コードと支店名を保持するMap
	 * @param 支店コードと売上金額を保持するMap
	 * @return 書き込み可否
	 */
	private static boolean writeFile(String path, String fileName, Map<String, String> branchNames, Map<String, Long> branchSales) {
		// ※ここに書き込み処理を作成してください。(処理内容3-1)

		return true;
	}

}
