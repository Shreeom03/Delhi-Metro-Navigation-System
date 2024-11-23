import java.util.*;
import java.io.*;

public class Graph_M {
	public class Vertex {
		HashMap<String, Integer> nbrs = new HashMap<>();
	}

	static HashMap<String, Vertex> vtces;

	public Graph_M() {
		vtces = new HashMap<>();
	}

	public int numVetex() {
		return this.vtces.size();
	}

	public boolean containsVertex(String vname) {
		return this.vtces.containsKey(vname);
	}

	public void addVertex(String vname) {
		Vertex vtx = new Vertex();
		vtces.put(vname, vtx);
	}

	public void removeVertex(String vname) {
		Vertex vtx = vtces.get(vname);
		ArrayList<String> keys = new ArrayList<>(vtx.nbrs.keySet());

		for (String key : keys) {
			Vertex nbrVtx = vtces.get(key);
			nbrVtx.nbrs.remove(vname);
		}

		vtces.remove(vname);
	}

	public int numEdges() {
		ArrayList<String> keys = new ArrayList<>(vtces.keySet());
		int count = 0;

		for (String key : keys) {
			Vertex vtx = vtces.get(key);
			count = count + vtx.nbrs.size();
		}

		return count / 2;
	}

	public boolean containsEdge(String vname1, String vname2) {
		Vertex vtx1 = vtces.get(vname1);
		Vertex vtx2 = vtces.get(vname2);

		if (vtx1 == null || vtx2 == null || !vtx1.nbrs.containsKey(vname2)) {
			return false;
		}

		return true;
	}

	public void addEdge(String vname1, String vname2, int value) {
		Vertex vtx1 = vtces.get(vname1);
		Vertex vtx2 = vtces.get(vname2);

		if (vtx1 == null || vtx2 == null || vtx1.nbrs.containsKey(vname2)) {
			return;
		}

		vtx1.nbrs.put(vname2, value);
		vtx2.nbrs.put(vname1, value);
	}

	public void removeEdge(String vname1, String vname2) {
		Vertex vtx1 = vtces.get(vname1);
		Vertex vtx2 = vtces.get(vname2);

		// check if the vertices given or the edge between these vertices exist or not
		if (vtx1 == null || vtx2 == null || !vtx1.nbrs.containsKey(vname2)) {
			return;
		}

		vtx1.nbrs.remove(vname2);
		vtx2.nbrs.remove(vname1);
	}

	public void display_Map() {
		System.out.println("\t Delhi Metro Map");
		System.out.println("\t------------------");
		System.out.println("----------------------------------------------------\n");
		ArrayList<String> keys = new ArrayList<>(vtces.keySet());

		for (String key : keys) {
			String str = key + " =>\n";
			Vertex vtx = vtces.get(key);
			ArrayList<String> vtxnbrs = new ArrayList<>(vtx.nbrs.keySet());

			for (String nbr : vtxnbrs) {
				str = str + "\t" + nbr + "\t";
				if (nbr.length() < 16)
					str = str + "\t";
				if (nbr.length() < 8)
					str = str + "\t";
				str = str + vtx.nbrs.get(nbr) + "\n";
			}
			System.out.println(str);
		}
		System.out.println("\t------------------");
		System.out.println("---------------------------------------------------\n");

	}

	public void display_Stations() {
		System.out.println("\n***********************************************************************\n");
		ArrayList<String> keys = new ArrayList<>(vtces.keySet());
		int i = 1;
		for (String key : keys) {
			System.out.println(i + ". " + key);
			i++;
		}
		System.out.println("\n***********************************************************************\n");
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public boolean hasPath(String vname1, String vname2, HashMap<String, Boolean> processed) {
		// DIR EDGE
		if (containsEdge(vname1, vname2)) {
			return true;
		}

		// MARK AS DONE
		processed.put(vname1, true);

		Vertex vtx = vtces.get(vname1);
		ArrayList<String> nbrs = new ArrayList<>(vtx.nbrs.keySet());

		// TRAVERSE THE NBRS OF THE VERTEX
		for (String nbr : nbrs) {

			if (!processed.containsKey(nbr))
				if (hasPath(nbr, vname2, processed))
					return true;
		}

		return false;
	}

	private class DijkstraPair implements Comparable<DijkstraPair> {
		String vname;
		String psf;
		int cost;

		@Override
		public int compareTo(DijkstraPair o) {
			return o.cost - this.cost;
		}
	}

	public DijkstraPair dijkstra(String src, String des, boolean nan) {
		DijkstraPair dp = new DijkstraPair();
		ArrayList<String> ans = new ArrayList<>();
		HashMap<String, DijkstraPair> map = new HashMap<>();

		Heap<DijkstraPair> heap = new Heap<>();

		for (String key : vtces.keySet()) {
			DijkstraPair np = new DijkstraPair();
			np.vname = key;
			// np.psf = "";
			np.cost = Integer.MAX_VALUE;

			if (key.equals(src)) {
				np.cost = 0;
				np.psf = key + "  ";
			}

			heap.add(np);
			map.put(key, np);
		}

		// keep removing the pairs while heap is not empty
		while (!heap.isEmpty()) {
			DijkstraPair rp = heap.remove();

			if (rp.vname.equals(des)) {
				dp = rp;
				break;
			}

			map.remove(rp.vname);

			ans.add(rp.vname);

			Vertex v = vtces.get(rp.vname);
			for (String nbr : v.nbrs.keySet()) {
				if (map.containsKey(nbr)) {
					int oc = map.get(nbr).cost;
					int nc;
					if (nan)
						nc = rp.cost + 120 + 40 * v.nbrs.get(nbr);
					else
						nc = rp.cost + v.nbrs.get(nbr);

					if (nc < oc) {
						DijkstraPair gp = map.get(nbr);
						gp.psf = rp.psf + nbr + "  ";
						gp.cost = nc;

						heap.updatePriority(gp);
					}
				}
			}
		}
		if (nan) {
			dp.psf = dp.psf + Integer.toString((dp.cost / 60));
		} else {
			dp.psf = dp.psf + Integer.toString(dp.cost);
		}
		return dp;
	}

	private class Pair {
		String vname;
		String psf;
		int min_dis;
		int min_time;
	}

	public ArrayList<String> get_Interchanges(String str) {
		ArrayList<String> arr = new ArrayList<>();
		String res[] = str.split("  ");
		arr.add(res[0]);
		int count = 0;
		for (int i = 1; i < res.length - 1; i++) {
			int index = res[i].indexOf('~');
			String s = res[i].substring(index + 1);

			if (s.length() == 2) {
				String prev = res[i - 1].substring(res[i - 1].indexOf('~') + 1);
				String next = res[i + 1].substring(res[i + 1].indexOf('~') + 1);

				if (prev.equals(next)) {
					arr.add(res[i]);
				} else {
					if (i == res.length - 2) {
						arr.add(res[i]);
						continue;
					}
					arr.add(res[i] + " ==> " + res[i + 1]);
					i++;
					count++;
				}
			} else {
				arr.add(res[i]);
			}
		}
		arr.add(Integer.toString(count));
		arr.add(res[res.length - 1]);
		return arr;
	}

	public static void Create_Metro_Map(Graph_M g) {
		g.addVertex("Noida Sector 62~B");
		g.addVertex("Botanical Garden~B");
		g.addVertex("Yamuna Bank~B");
		g.addVertex("Rajiv Chowk~BY");
		g.addVertex("Vaishali~B");
		g.addVertex("Moti Nagar~B");
		g.addVertex("Janak Puri West~BO");
		g.addVertex("Dwarka Sector 21~B");
		g.addVertex("Huda City Center~Y");
		g.addVertex("Saket~Y");
		g.addVertex("Vishwavidyalaya~Y");
		g.addVertex("Chandni Chowk~Y");
		g.addVertex("New Delhi~YO");
		g.addVertex("AIIMS~Y");
		g.addVertex("Shivaji Stadium~O");
		g.addVertex("DDS Campus~O");
		g.addVertex("IGI Airport~O");
		g.addVertex("Rajouri Garden~BP");
		g.addVertex("Netaji Subhash Place~PR");
		g.addVertex("Punjabi Bagh West~P");

		g.addEdge("Noida Sector 62~B", "Botanical Garden~B", 8);
		g.addEdge("Botanical Garden~B", "Yamuna Bank~B", 10);
		g.addEdge("Yamuna Bank~B", "Vaishali~B", 8);
		g.addEdge("Yamuna Bank~B", "Rajiv Chowk~BY", 6);
		g.addEdge("Rajiv Chowk~BY", "Moti Nagar~B", 9);
		g.addEdge("Moti Nagar~B", "Janak Puri West~BO", 7);
		g.addEdge("Janak Puri West~BO", "Dwarka Sector 21~B", 6);
		g.addEdge("Huda City Center~Y", "Saket~Y", 15);
		g.addEdge("Saket~Y", "AIIMS~Y", 6);
		g.addEdge("AIIMS~Y", "Rajiv Chowk~BY", 7);
		g.addEdge("Rajiv Chowk~BY", "New Delhi~YO", 1);
		g.addEdge("New Delhi~YO", "Chandni Chowk~Y", 2);
		g.addEdge("Chandni Chowk~Y", "Vishwavidyalaya~Y", 5);
		g.addEdge("New Delhi~YO", "Shivaji Stadium~O", 2);
		g.addEdge("Shivaji Stadium~O", "DDS Campus~O", 7);
		g.addEdge("DDS Campus~O", "IGI Airport~O", 8);
		g.addEdge("Moti Nagar~B", "Rajouri Garden~BP", 2);
		g.addEdge("Punjabi Bagh West~P", "Rajouri Garden~BP", 2);
		g.addEdge("Punjabi Bagh West~P", "Netaji Subhash Place~PR", 3);
	}

	public static String[] printCodelist() {
		System.out.println("List of station along with their codes:\n");
		ArrayList<String> keys = new ArrayList<>(vtces.keySet());
		int i = 1, j = 0, m = 1;
		StringTokenizer stname;
		String temp = "";
		String codes[] = new String[keys.size()];
		char c;
		for (String key : keys) {
			stname = new StringTokenizer(key);
			codes[i - 1] = "";
			j = 0;
			while (stname.hasMoreTokens()) {
				temp = stname.nextToken();
				c = temp.charAt(0);
				while (c > 47 && c < 58) {
					codes[i - 1] += c;
					j++;
					c = temp.charAt(j);
				}
				if ((c < 48 || c > 57) && c < 123)
					codes[i - 1] += c;
			}
			if (codes[i - 1].length() < 2)
				codes[i - 1] += Character.toUpperCase(temp.charAt(1));

			System.out.print(i + ". " + key + "\t");
			if (key.length() < (22 - m))
				System.out.print("\t");
			if (key.length() < (14 - m))
				System.out.print("\t");
			if (key.length() < (6 - m))
				System.out.print("\t");
			System.out.println(codes[i - 1]);
			i++;
			if (i == (int) Math.pow(10, m))
				m++;
		}
		return codes;
	}

	public static void main(String[] args) throws IOException {
		Graph_M g = new Graph_M();
		Create_Metro_Map(g);

		System.out.println("\n\t\t\t****WELCOME TO THE METRO APP*****");
		BufferedReader inp = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			System.out.println("\t\t\t\t~~LIST OF ACTIONS~~\n\n");
			System.out.println("1. LIST ALL THE STATIONS IN THE MAP");
			System.out.println("2. SHOW THE METRO MAP");
			System.out.println("3. GET SHORTEST DISTANCE FROM A 'SOURCE' STATION TO 'DESTINATION' STATION");
			System.out.println("4. GET SHORTEST TIME TO REACH FROM A 'SOURCE' STATION TO 'DESTINATION' STATION");
			System.out.println(
					"5. GET SHORTEST PATH (DISTANCE WISE) TO REACH FROM A 'SOURCE' STATION TO 'DESTINATION' STATION");
			System.out.println(
					"6. GET SHORTEST PATH (TIME WISE) TO REACH FROM A 'SOURCE' STATION TO 'DESTINATION' STATION");
			System.out.println("7. GET COST TO REACH FROM A 'SOURCE' STATION TO 'DESTINATION' STATION");
			System.out.println("8. EXIT THE MENU");
			System.out.print("\nENTER YOUR CHOICE FROM THE ABOVE LIST (1 to 8) : ");
			int choice = -1;
			try {
				choice = Integer.parseInt(inp.readLine());
			} catch (Exception e) {
			}
			System.out.print("\n***********************************************************\n");
			if (choice == 8) {
				System.out.println("Thanks for choosing Delhi Metro!");
				System.exit(0);
			}
			switch (choice) {
				case 1:
					g.display_Stations();
					break;

				case 2:
					g.display_Map();
					break;

				case 3:
					ArrayList<String> keys = new ArrayList<>(vtces.keySet());
					String codes[] = printCodelist();
					System.out.println(
							"\n1. TO ENTER SERIAL NO. OF STATIONS\n2. TO ENTER CODE OF STATIONS\n3. TO ENTER NAME OF STATIONS\n");
					System.out.println("ENTER YOUR CHOICE:");
					int ch = Integer.parseInt(inp.readLine());
					int j;

					String st1 = "", st2 = "";
					System.out.println("ENTER THE SOURCE AND DESTINATION STATIONS");
					if (ch == 1) {
						st1 = keys.get(Integer.parseInt(inp.readLine()) - 1);
						st2 = keys.get(Integer.parseInt(inp.readLine()) - 1);
					} else if (ch == 2) {
						String a, b;
						a = (inp.readLine()).toUpperCase();
						for (j = 0; j < keys.size(); j++)
							if (a.equals(codes[j]))
								break;
						st1 = keys.get(j);
						b = (inp.readLine()).toUpperCase();
						for (j = 0; j < keys.size(); j++)
							if (b.equals(codes[j]))
								break;
						st2 = keys.get(j);
					} else if (ch == 3) {
						st1 = inp.readLine();
						st2 = inp.readLine();
					} else {
						System.out.println("Invalid choice");
						System.exit(0);
					}

					HashMap<String, Boolean> processed = new HashMap<>();
					if (!g.containsVertex(st1) || !g.containsVertex(st2) || !g.hasPath(st1, st2, processed))
						System.out.println("THE INPUTS ARE INVALID");
					else
						System.out.println("SHORTEST DISTANCE FROM " + st1 + " TO " + st2 + " IS "
								+ g.dijkstra(st1, st2, false).cost + "KM\n");
					break;

				case 4:
					ArrayList<String> keys1 = new ArrayList<>(vtces.keySet());
					String codes1[] = printCodelist();
					System.out.println(
							"\n1. TO ENTER SERIAL NO. OF STATIONS\n2. TO ENTER CODE OF STATIONS\n3. TO ENTER NAME OF STATIONS\n");
					System.out.println("ENTER YOUR CHOICE:");
					int ch1 = Integer.parseInt(inp.readLine());
					int j1;

					String sat1 = "", sat2 = "";
					System.out.println("ENTER THE SOURCE AND DESTINATION STATIONS");
					if (ch1 == 1) {
						sat1 = keys1.get(Integer.parseInt(inp.readLine()) - 1);
						sat2 = keys1.get(Integer.parseInt(inp.readLine()) - 1);
					} else if (ch1 == 2) {
						String a, b;
						a = (inp.readLine()).toUpperCase();
						for (j = 0; j < keys1.size(); j++)
							if (a.equals(codes1[j]))
								break;
						sat1 = keys1.get(j);
						b = (inp.readLine()).toUpperCase();
						for (j = 0; j < keys1.size(); j++)
							if (b.equals(codes1[j]))
								break;
						sat2 = keys1.get(j);
					} else if (ch1 == 3) {
						sat1 = inp.readLine();
						sat2 = inp.readLine();
					} else {
						System.out.println("Invalid choice");
						System.exit(0);
					}

					HashMap<String, Boolean> processed1 = new HashMap<>();
					if (!g.containsVertex(sat1) || !g.containsVertex(sat2) || !g.hasPath(sat1, sat2, processed1))
						System.out.println("THE INPUTS ARE INVALID");

					System.out.println("SHORTEST TIME FROM (" + sat1 + ") TO (" + sat2 + ") IS "
							+ g.dijkstra(sat1, sat2, true).cost / 60 + " MINUTES\n\n");
					break;

				case 5:
					ArrayList<String> keys2 = new ArrayList<>(vtces.keySet());
					String codes2[] = printCodelist();
					System.out.println(
							"\n1. TO ENTER SERIAL NO. OF STATIONS\n2. TO ENTER CODE OF STATIONS\n3. TO ENTER NAME OF STATIONS\n");
					System.out.println("ENTER YOUR CHOICE:");
					int ch2 = Integer.parseInt(inp.readLine());
					int j2;

					String s1 = "", s2 = "";
					System.out.println("ENTER THE SOURCE AND DESTINATION STATIONS");
					if (ch2 == 1) {
						s1 = keys2.get(Integer.parseInt(inp.readLine()) - 1);
						s2 = keys2.get(Integer.parseInt(inp.readLine()) - 1);
					} else if (ch2 == 2) {
						String a, b;
						a = (inp.readLine()).toUpperCase();
						for (j = 0; j < keys2.size(); j++)
							if (a.equals(codes2[j]))
								break;
						s1 = keys2.get(j);
						b = (inp.readLine()).toUpperCase();
						for (j = 0; j < keys2.size(); j++)
							if (b.equals(codes2[j]))
								break;
						s2 = keys2.get(j);
					} else if (ch2 == 3) {
						s1 = inp.readLine();
						s2 = inp.readLine();
					} else {
						System.out.println("Invalid choice");
						System.exit(0);
					}

					HashMap<String, Boolean> processed2 = new HashMap<>();
					if (!g.containsVertex(s1) || !g.containsVertex(s2) || !g.hasPath(s1, s2, processed2))
						System.out.println("THE INPUTS ARE INVALID");
					else {
						ArrayList<String> str = g.get_Interchanges(g.dijkstra(s1, s2, false).psf);
						int len = str.size();
						System.out.println("SOURCE STATION : " + s1);
						System.out.println("DESTINATION STATION : " + s2);
						System.out.println("DISTANCE : " + str.get(len - 1) + " K.M.");
						System.out.println("NUMBER OF INTERCHANGES : " + str.get(len - 2));
						// System.out.println(str);
						System.out.println("~~~~~~~~~~~~~");
						System.out.println("START  ==>  " + str.get(0));
						for (int i = 1; i < len - 3; i++) {
							System.out.println(str.get(i));
						}
						System.out.print(str.get(len - 3) + "   ==>    END");
						System.out.println("\n~~~~~~~~~~~~~");
					}
					break;

				case 6:
					ArrayList<String> keys3 = new ArrayList<>(vtces.keySet());
					String codes3[] = printCodelist();
					System.out.println(
							"\n1. TO ENTER SERIAL NO. OF STATIONS\n2. TO ENTER CODE OF STATIONS\n3. TO ENTER NAME OF STATIONS\n");
					System.out.println("ENTER YOUR CHOICE:");
					int ch3 = Integer.parseInt(inp.readLine());
					int j3;

					String ss1 = "", ss2 = "";
					System.out.println("ENTER THE SOURCE AND DESTINATION STATIONS");
					if (ch3 == 1) {
						ss1 = keys3.get(Integer.parseInt(inp.readLine()) - 1);
						ss2 = keys3.get(Integer.parseInt(inp.readLine()) - 1);
					} else if (ch3 == 2) {
						String a, b;
						a = (inp.readLine()).toUpperCase();
						for (j = 0; j < keys3.size(); j++)
							if (a.equals(codes3[j]))
								break;
						ss1 = keys3.get(j);
						b = (inp.readLine()).toUpperCase();
						for (j = 0; j < keys3.size(); j++)
							if (b.equals(codes3[j]))
								break;
						ss2 = keys3.get(j);
					} else if (ch3 == 3) {
						ss1 = inp.readLine();
						ss2 = inp.readLine();
					} else {
						System.out.println("Invalid choice");
						System.exit(0);
					}

					HashMap<String, Boolean> processed3 = new HashMap<>();
					if (!g.containsVertex(ss1) || !g.containsVertex(ss2) || !g.hasPath(ss1, ss2, processed3))
						System.out.println("THE INPUTS ARE INVALID");
					else {
						ArrayList<String> str = g.get_Interchanges(g.dijkstra(ss1, ss2, true).psf);
						int len = str.size();
						System.out.println("SOURCE STATION : " + ss1);
						System.out.println("DESTINATION STATION : " + ss2);
						System.out.println("TIME : " + str.get(len - 1) + " MINUTES");
						System.out.println("NUMBER OF INTERCHANGES : " + str.get(len - 2));
						// System.out.println(str);
						System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
						System.out.println("START  ==>  " + str.get(0));
						for (int i = 1; i < len - 3; i++) {
							System.out.println(str.get(i));
						}
						System.out.print(str.get(len - 3) + "   ==>    END");
						System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
					}
					break;
				case 7:
					ArrayList<String> keys4 = new ArrayList<>(vtces.keySet());
					String codes4[] = printCodelist();
					System.out.println(
							"\n1. TO ENTER SERIAL NO. OF STATIONS\n2. TO ENTER CODE OF STATIONS\n3. TO ENTER NAME OF STATIONS\n");
					System.out.println("ENTER YOUR CHOICE:");
					int ch4 = Integer.parseInt(inp.readLine());
					int j4;

					String sst1 = "", sst2 = "";
					System.out.println("ENTER THE SOURCE AND DESTINATION STATIONS");
					if (ch4 == 1) {
						sst1 = keys4.get(Integer.parseInt(inp.readLine()) - 1);
						sst2 = keys4.get(Integer.parseInt(inp.readLine()) - 1);
					} else if (ch4 == 2) {
						String a, b;
						a = (inp.readLine()).toUpperCase();
						for (j = 0; j < keys4.size(); j++)
							if (a.equals(codes4[j]))
								break;
						sst1 = keys4.get(j);
						b = (inp.readLine()).toUpperCase();
						for (j = 0; j < keys4.size(); j++)
							if (b.equals(codes4[j]))
								break;
						sst2 = keys4.get(j);
					} else if (ch4 == 3) {
						sst1 = inp.readLine();
						sst2 = inp.readLine();
					} else {
						System.out.println("Invalid choice");
						System.exit(0);
					}

					HashMap<String, Boolean> processed4 = new HashMap<>();
					if (!g.containsVertex(sst1) || !g.containsVertex(sst2) || !g.hasPath(sst1, sst2, processed4))
						System.out.println("THE INPUTS ARE INVALID");
					else {
						System.out.print("COST FOR " + sst1 + " TO " + sst2 + " IS ");
						int dist = g.dijkstra(sst1, sst2, false).cost;

						if (dist > 0 && dist <= 2) {
							System.out.print(10);
						} else if (dist > 2 && dist <= 5) {
							System.out.print(20);
						} else if (dist > 5 && dist <= 12) {
							System.out.print(30);
						} else if (dist > 12 && dist <= 21) {
							System.out.print(40);
						} else {
							System.out.print(50);
						}

						System.out.println(" Rupees");
					}
					break;
				default:
					System.out.println("Please enter a valid option! ");
					System.out.println("The options you can choose are from 1 to 6. ");

			}
		}

	}
}
