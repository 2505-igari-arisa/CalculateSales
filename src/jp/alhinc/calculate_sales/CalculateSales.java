package jp.alhinc.calculate_sales;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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
		File[] files = new File(args[0]).listFiles();

		//条件に一致したファイルだけを入れるリスト
		List<File> rcdFiles = new ArrayList<>();

		//取得したファイルに格納されているファイル名を取得
		for(int i = 0; i < files.length; i++){

			//ファイル名の条件が一致しているか（0-9の8桁で始まって.rcdで終わる）
			if(files[i].getName().matches("^[0-9]{8}[.]rcd$")){

				//trueならrcdFilesリストに入れる※falsなら入らない
				rcdFiles.add(files[i]);
			}
		}
		//Listの中身をソートする
		Collections.sort(rcdFiles);

		//売上ファイルが連番になっているか確認する：エラー処理2-1
		for(int i = 0; i < rcdFiles.size() - 1; i++) {
			//リストに入れた売上ファイルのi番目からファイル名をgetして
			//先頭から数字の8文字を切り出しint型に変換
			int former = Integer.parseInt(rcdFiles.get(i).getName().substring(0, 8));
			int latter = Integer.parseInt(rcdFiles.get(i + 1).getName().substring(0, 8));

				if((latter - former) != 1) {
					System.out.println("売上ファイル名が連番になっていません");
					return;
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
				File openFile = new File(args[0], rcdFiles.get(i).getName());
				FileReader rcdfr = new FileReader(openFile);
				rcdbr = new BufferedReader(rcdfr);

				// 売上ファイルを一行ずつ読み込む
				String rcdLine;
				while((rcdLine = rcdbr.readLine()) != null) {

					//一行ごとに内容を格納するリストの中に入れる※rcdCodeFilesリストに入れる
					rcdCodeFiles.add(rcdLine);
				}

					//該当する支店コードがあるか確認する：エラー処理2-3
					if (!branchSales.containsKey(rcdCodeFiles.get(0))) {
						System.out.println("<" + rcdFiles.get(i).getName() + ">の支店コードが不正です");
						return;
					}

					//売上ファイルのフォーマットを確認する：エラー処理2-4
					if(rcdCodeFiles.size() != 2) {
						System.out.println("<" + rcdFiles.get(i).getName() + ">のフォーマットが不正です");
						return;
					}

				//売上金額のみ支店コードと売上金額を保持するMapと同じ<Long型>にする
				long fileSale = Long.parseLong(rcdCodeFiles.get(1));

				//Map(HashMap)から値を取得する
				//読み込んできた売り上げファイル内の一致する支店コードの売上金額
				Long saleAmount = branchSales.get(rcdCodeFiles.get(0)) + fileSale;

					//売上⾦額が11桁以上になっていないか確認する：エラー処理2-2
					if(saleAmount >= 10000000000L) {
						System.out.println("合計金額が10桁を超えました");
						return;
					}

				//加算した売上金額をbranchSalesに上書きする
				branchSales.put(rcdCodeFiles.get(0), saleAmount);


			} catch(IOException e) {
				System.out.println(UNKNOWN_ERROR);
				return;
			} finally {
				// ファイルを開いている場合
				if(rcdbr != null) {
					try {
						// ファイルを閉じる
						rcdbr.close();
					} catch(IOException e) {
						System.out.println(UNKNOWN_ERROR);
						return;
					}
				}
			}

		}
		if(!writeFile(args[0], FILE_NAME_BRANCH_OUT, branchNames, branchSales)) { //支店別集計ファイル書き込み処理

			return;
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

			//ファイルの存在を確認する：エラー処理1
			if(!file.exists()) {
				System.out.println(FILE_NOT_EXIST);
				return false;
			}

			FileReader fr = new FileReader(file);
			br = new BufferedReader(fr);

			String line;
			// 支店定義ファイルを一行ずつ読み込む
			while((line = br.readLine()) != null) {

				// ※ここの読み込み処理を変更してください。(処理内容1-2)
				//カンマの位置で分割
				String[] items = line.split(",");

				//支店定義ファイルのフォーマットを確認する：エラー処理1-2
				if((items.length != 2) || (!items[0].matches("^[0-9]{3}$"))){
					System.out.println(FILE_INVALID_FORMAT);
					return false;
				}

				//支店コードと支店名を保持するMapに追加する2つの情報をputの引数として指定
			    branchNames.put(items[0], items[1]);
			    branchSales.put(items[0], 0L);

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
		//ファイルを書き込む場所
		File totalFile = new File(path, fileName);

		BufferedWriter bw = null;

		try {
			FileWriter fw = new FileWriter(totalFile);
			bw = new BufferedWriter(fw);

			//String key で branchNamesから支店コード(key)を取得
			for(String key : branchNames.keySet()) {
				bw.write(key + "," + branchNames.get(key) + "," + branchSales.get(key));
				bw.newLine();

			}

		}catch(IOException e) {
			System.out.println(UNKNOWN_ERROR);
			return false;

		} finally {
			// ファイルを開いている場合
			if(bw != null) {
				try {
					// ファイルを閉じる
					bw.close();

				} catch(IOException e) {
					System.out.println(UNKNOWN_ERROR);

					return false;
				}
			}
		}

		return true;
	}

}
