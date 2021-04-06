package ubc.cosc322.search;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import ubc.cosc322.board.tiles.Arrow;
import ubc.cosc322.board.tiles.BoardTile;
import ubc.cosc322.board.tiles.Queen;

public class ReinforcementLearning {
	private static ReinforcementLearning instance = new ReinforcementLearning();
	private HashMap<String, VisitMoveTuple> memory = new HashMap<String, VisitMoveTuple>();

	public static ReinforcementLearning getInstance() {
		return instance;
	}

	public ReinforcementLearning(HashMap<String, VisitMoveTuple> memory) {
		this.memory = memory;
	}

	public ReinforcementLearning() {
		this.memory = new HashMap<String, VisitMoveTuple>();
		deserialize();
		System.out.println("Mind CORE loaded successfully!");
	}

	public String hash(BoardTile[][] state) {
		byte[][] byteState = convertState(state);
		String stateString = Arrays.deepToString(byteState);

		MessageDigest md = null;
		try
		{
			md = MessageDigest.getInstance("SHA-256");
		}
		catch(Exception ex) {}
		md.update(stateString.getBytes());
		byte[] digest = md.digest();

		return Base64.getEncoder().encodeToString(digest);
	}

	public void addData(BoardTile[][] state, int numWins, int numVisits) {
		String hash = hash(state);
		VisitMoveTuple data = this.memory.get(hash);
		if(data == null)
			data = new VisitMoveTuple(0, 0);
		
		data.numWins += numWins;
		data.numVisit += numVisits;

		this.memory.put(hash, data);
	}

	public VisitMoveTuple getData(BoardTile[][] state) {
		VisitMoveTuple data = this.memory.get(hash(state));
		if(data == null)
		{
			data = new VisitMoveTuple(0, 0);
		}
		return data;
	}

	public void serialize() {
		try(
			FileOutputStream f = new FileOutputStream(new File("D:/mind.core"));
			ObjectOutputStream o = new ObjectOutputStream(f);
		) {
			o.writeObject(memory);
		}
		catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            System.out.println("Error initializing stream");
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

	private void deserialize() {
		try(
			FileInputStream f = new FileInputStream(new File("D:/mind.core"));
			ObjectInputStream o = new ObjectInputStream(f);
		) {
			memory = (HashMap<String, VisitMoveTuple>)o.readObject();
		}
		catch (FileNotFoundException e) {
			System.out.println("Mind File not found !!");
            this.memory = new HashMap<String, VisitMoveTuple>();
        } catch (IOException e) {
            System.out.println("Error initializing stream");
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

	public byte[][] convertState(BoardTile[][] board) {
		byte[][] state = new byte[10][10];

		for(int i = 0; i < 10; i++)
		{
			for(int j = 0; j < 10; j++)
			{
				if(board[i][j] instanceof Queen)
				{
					if(((Queen)board[i][j]).friendly)
						state[i][j] = 1;
					else
						state[i][j] = -1;
				}
				else if(board[i][j] instanceof Arrow)
				{
					state[i][j] = 2;
				}
				else
				{
					state[i][j] = 0;
				}
			}
		}

		return state;
	}
}
