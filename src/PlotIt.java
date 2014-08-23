import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Stack;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

class ETNode {
	int tag;// tag = 0->binary operator
	// tag = 1->variable
	// tag = 2->constant
	// tag = 4->unary operator
	char operator;
	char var;
	double operand;
	ETNode left, right;

	ETNode() {

	}
};

public class PlotIt extends JFrame implements Runnable, KeyListener,
		MouseListener, MouseMotionListener {

	private static final long serialVersionUID = 1L;
	char c;

	private JTextField textField;
	private JPanel panel;

	double p_X = (float) 0, p_Y = (float) 600.0, X_increment;

	private static ETNode ETree;
	private static Stack<ETNode> operatorStack, treeStack;

	private static ETNode postfixString[];
	private static int postfixSize = 0, O_X = 400, O_Y = 400,
			num_of_divisions = 20, left_boundary = -400,
			right_boundary = 400;
			// left_index = (-1 * num_of_divisions / 2),
			// right_index = (num_of_divisions / 2),
			
	private static double left_index = (-1 * num_of_divisions / 2),
			right_index = (num_of_divisions / 2),upper_index = (num_of_divisions / 2), lower_index = (-1
					* num_of_divisions / 2), temp_left_index, temp_right_index,
			temp_lower_index, temp_upper_index;;
	double lastNum, scale = 1.0, max_num = 20.0, division, left_x = -10.0,
			right_x = 10.0;
	int sphere_x = 0, ball_par = 10, level_variation = 0;

	HashMap<Character, Boolean> operator;
	HashMap<Character, Integer> priority;

	double n_X, n_Y, magni = 10.0;
	public static String expression;
	Thread t;
	static PlotIt b;

	private Scanner scan;

	public static void main(String args[]) {
		b = new PlotIt();

	}

	public void update(Graphics g) {
		paint(g);
	}

	PlotIt() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setFocusable(true);

		panel = new JPanel();
		panel.setLayout(new BorderLayout());

		textField = new JTextField();
		panel.add(textField, BorderLayout.WEST);
		this.setLayout(new FlowLayout());
		add(panel);
		this.pack();
		operator = new HashMap<Character, Boolean>();
		priority = new HashMap<Character, Integer>();
		operatorStack = new Stack<ETNode>();
		treeStack = new Stack<ETNode>();
		operator.put('^', true);
		operator.put('-', true);
		operator.put('+', true);
		operator.put('*', true);
		operator.put('/', true);
		priority.put('-', 1);
		priority.put('+', 1);
		priority.put('*', 2);
		priority.put('/', 3);
		priority.put('^', 4);
		operator.put('s', true);
		priority.put('s', 5);
		operator.put('c', true);
		priority.put('c', 5);
		operator.put('t', true);
		priority.put('t', 5);
		operator.put('?', true);
		priority.put('?', -1);
		operator.put('(', true);
		priority.put('(', -1);
		operator.put(')', true);
		priority.put(')', -1);
		System.out.println(Math.E);
		scan = new Scanner(System.in);
		System.out.println("Enter the Expression in terms of variable x");
		expression = scan.nextLine();

		postfixString = new ETNode[expression.length()];
		ConvertToPostfix(expression);
		System.out.println("length: " + expression.length());
		while (priority.get(operatorStack.peek().operator) != -1) {
			postfixString[postfixSize++] = operatorStack.pop();
		}
		System.out.println("The infix expression is:");
		for (int i = 0; i < postfixSize; i++) {
			if (postfixString[i].tag == 0 || postfixString[i].tag == 4)
				System.out.print(postfixString[i].operator + " ");
			else if (postfixString[i].tag == 1)
				System.out.print(postfixString[i].var + " ");
			else if (postfixString[i].tag == 2)
				System.out.print(postfixString[i].operand + " ");
		}
		constructExpressionTree();
		ETree = treeStack.pop();
		setSize(800, 800);
		addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		setFocusable(true);
		setVisible(true);

		// setDoubleBuffered(true);
		repaint();
	}

	private void constructExpressionTree() {
		for (int i = 0; i < postfixSize; i++) {
			if (postfixString[i].tag == 1 || postfixString[i].tag == 2) {
				treeStack.push(postfixString[i]);
			} else if (postfixString[i].tag == 4) {
				ETNode l;
				l = treeStack.pop();
				postfixString[i].left = l;
				postfixString[i].right = null;// unary operators have only the
												// left child..
				treeStack.push(postfixString[i]);
			} else {
				ETNode l, r;
				r = treeStack.pop();
				l = treeStack.pop();
				postfixString[i].left = l;
				postfixString[i].right = r;
				treeStack.push(postfixString[i]);
			}
		}
	}

	private void ConvertToPostfix(String expression) {
		int i = 0;
		char cur;
		ETNode p = new ETNode();
		p.tag = 0;
		p.operator = '?';
		operatorStack.push(p);
		while (i < expression.length()) {
			cur = expression.charAt(i);
			if (Character.isLetterOrDigit(cur)) {// current char is not an
													// operator
				if (cur == 'x') {
					ETNode t = new ETNode();
					t.tag = 1;
					t.var = cur;
					t.left = null;
					t.right = null;
					postfixString[postfixSize++] = t;
				} else if (cur == 'e') {
					ETNode t = new ETNode();
					t.tag = 2;
					t.operand = Math.E;
					t.left = null;
					t.right = null;
					postfixString[postfixSize++] = t;
				} else if (cur == 's') {// its a sine operator
					i += 2;
					ETNode t = new ETNode();
					t.tag = 4;
					t.operator = 's';
					t.left = null;
					t.right = null;
					operatorStack.push(t);
				} else if (cur == 'c') {// its a sine operator
					i += 2;
					ETNode t = new ETNode();
					t.tag = 4;
					t.operator = 'c';
					t.left = null;
					t.right = null;
					operatorStack.push(t);
				} else if (cur == 't') {// its a sine operator
					i += 2;
					ETNode t = new ETNode();
					t.tag = 4;
					t.operator = 't';
					t.left = null;
					t.right = null;
					operatorStack.push(t);
				} else {// its a digit

					i = extractNumber(expression, i);// advance to the next
														// position after the
														// number constant
					ETNode t = new ETNode();
					t.tag = 2;
					t.operand = lastNum;
					t.left = null;
					t.right = null;
					postfixString[postfixSize++] = t;
				}

			}

			else {

				if (cur == '(') {
					ETNode t = new ETNode();
					t.tag = 0;
					t.operator = cur;
					t.left = null;
					t.right = null;
					operatorStack.push(t);
				}

				else if (cur == ')') {
					while ((operatorStack.peek().operator) != '(') {
						System.out.println("popping "
								+ operatorStack.peek().operator);
						postfixString[postfixSize++] = operatorStack.pop();
					}
					operatorStack.pop();// pops the remaining '('
				}

				else {// its an operand
					while (priority.get(operatorStack.peek().operator) > priority
							.get(cur)) {
						postfixString[postfixSize++] = operatorStack.pop();
					}
					ETNode t = new ETNode();
					t.tag = 0;
					t.operator = cur;
					t.left = null;
					t.right = null;
					operatorStack.push(t);
				}
			}
			i++;
		}
	}

	private int extractNumber(String expression2, int i) {
		lastNum = 0.0;
		char c = '@';
		while (i < expression.length()) {
			c = expression.charAt(i);
			if (Character.isDigit(c)) {
				lastNum = (lastNum * 10) + (c - '0');
				i++;
			} else
				break;
		}
		double decimal = 0.0;
		if (c == '.') {
			decimal = 0.0;
			int pow = 1;
			i++;
			while (i < expression.length()) {
				c = expression.charAt(i);
				if (Character.isDigit(c)) {
					decimal += ((double) (c - '0')) / Math.pow(10, pow);
					pow++;
					i++;
				} else
					break;
			}
		}
		lastNum += decimal;
		return i - 1;
	}

	public void paint(Graphics g) {
		super.paint(g);// clears the screen
		// System.out.println("Scale:" + scale + ", division = " + division
		division = 800 / num_of_divisions;

		// Calculate the coordinate of the origin and the axes
		O_X = (400 - (int) (((right_index + left_index) / 2.0) * division));
		O_Y = (400 + (int) (((upper_index + lower_index) / 2.0) * division));

		double i = left_index;
		int j = (-1 * num_of_divisions / 2);
		while (i <= right_index) {
			int index = (int) (j * division);
			g.setColor(Color.CYAN);
			g.drawLine((int) (index) + 400, 0, (int) (index) + 400, 800);
			g.setColor(Color.BLACK);
			g.drawLine((int) ((float) (index)) + 400, O_Y - 5,
					(int) ((float) (index)) + 400, O_Y + 5);
			g.setColor(Color.MAGENTA);
			if (i % 2 == 0) {
				g.drawString(Float.toString((float) (i)), index + 400, O_Y + 12);
			} else {
				g.drawString(Float.toString((float) (i)), index + 400, O_Y + 12
						+ level_variation);
			}
			j++;
			i += scale;
		}
		i = lower_index;
		j = (-1 * num_of_divisions / 2);
		while (i <= upper_index) {
			int index = (int) (j * division);
			g.setColor(Color.CYAN);
			g.drawLine(0, 400 - (int) ((float) (index)), 800,
					400 - (int) ((float) (index)));
			g.setColor(Color.BLACK);
			g.drawLine(O_X - 5, 400 - (int) ((float) (index)), O_X + 5,
					400 - (int) ((float) (index)));
			g.setColor(Color.MAGENTA);
			g.drawString(Float.toString((float) (i)), O_X + 12, 400 - index);
			j++;
			i += scale;
		}

		// Drawing the track-balls
		g.setColor(Color.BLACK);
		double sphere_y = evaluateExpression(ETree, (int) (sphere_x - O_X)
				* (scale / division));
		// System.out.println("x: " + (int)(sphere_x - O_X)*(scale / division) +
		// ", y: " + sphere_y);
		g.fillOval(sphere_x - ball_par / 2, O_Y
				- (int) (sphere_y / scale * division) - ball_par / 2, ball_par,
				ball_par);

		// DRAWING THE AXES..
		g.setColor(Color.BLACK);
		g.drawLine(0, O_Y, 800, O_Y);
		g.drawLine(O_X, 0, O_X, 800);
		g.setColor(Color.RED);
		// double i = 0;

		// System.out.println("calculating from " + left_index + "to"
		// + right_index);
		p_X = (left_index - 2) * scale;// left-most x-coordinate..
		while (p_X <= (right_index + 2) * scale) {// upto right-most
													// x-coordinate..
			n_X = p_X + .1;
			n_Y = evaluateExpression(ETree, n_X);
			// n_Y = magni * Math.pow(n_X,n_X);
			g.drawLine((int) ((p_X / scale * division) + O_X),
					(O_Y - (int) (p_Y / scale * division)),
					((int) (n_X / scale * division) + O_X), (O_Y - (int) (n_Y
							/ scale * division)));
			// System.out.println("point: " + n_X + " " + n_Y + "->" + " "
			// +((p_X * division) + O_X) + " " + (O_Y - (p_Y * division)) );
			p_X = n_X;
			p_Y = n_Y;
		}
	}

	private double evaluateExpression(ETNode root, double x) {
		if (root.tag == 0) {
			if (root.operator == '^') {
				return (Math.pow(evaluateExpression(root.left, x),
						evaluateExpression(root.right, x)));
			}
			if (root.operator == '+') {
				return (evaluateExpression(root.left, x) + evaluateExpression(
						root.right, x));
			}
			if (root.operator == '-') {
				return (evaluateExpression(root.left, x) - evaluateExpression(
						root.right, x));
			}
			if (root.operator == '*') {
				return (evaluateExpression(root.left, x) * evaluateExpression(
						root.right, x));
			}
			if (root.operator == '/') {
				return (evaluateExpression(root.left, x) / evaluateExpression(
						root.right, x));
			}
		} else if (root.tag == 1) {
			return (double) (x);
		} else if (root.tag == 4) {
			if (root.operator == 's') {
				return (Math.sin(evaluateExpression(root.left, x)));
			} else if (root.operator == 'c') {
				return (Math.cos(evaluateExpression(root.left, x)));
			} else if (root.operator == 't') {
				return (Math.tan(evaluateExpression(root.left, x)));
			}
		} else {
			return (double) (root.operand);
		}
		return 0;
	}

	@Override
	public void run() {

	}

	@Override
	public void keyPressed(KeyEvent e) {
		// System.out.println("fuck yea");
		if (e.getKeyCode() == KeyEvent.VK_UP) {
			lower_index += 1;
			upper_index += 1;
			// O_Y += division;
			repaint();
		} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			lower_index -= 1;
			upper_index -= 1;
			// O_Y -= division;
			repaint();
		} else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			// left_boundary -= division;
			// right_boundary -= division;
			// left_x -= 0.1;
			// right_x -= 0.1;
			left_index -= 1;
			right_index -= 1;
			// O_X += division;
			repaint();
		} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			// left_boundary += division;
			// right_boundary += division;
			// left_x += 0.1;
			// right_x += 0.1;
			left_index += 1;
			right_index += 1;
			// O_X -= division;
			repaint();
		} else if (e.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
			// System.out.println("asf");

			num_of_divisions -= 2;
			if (num_of_divisions < 25) {
				level_variation = 0;
			}
			if (num_of_divisions >= 1) {
				left_index += 1;
				right_index -= 1;
				upper_index -= 1;
				lower_index += 1;
			} else {
				num_of_divisions += 2;
			}
			repaint();
		} else if (e.getKeyCode() == KeyEvent.VK_PAGE_UP) {
			if (num_of_divisions < 50) {
				num_of_divisions += 2;
				left_index -= 1;
				right_index += 1;
				upper_index += 1;
				lower_index -= 1;
				if (num_of_divisions < 25) {
					level_variation = 0;
				} else {
					// num_of_divisions -= 2;
					level_variation = num_of_divisions / 3;
				}
				repaint();
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		 // // TODO Auto-generated method stub
		 int delta_x = e.getX();
		 int delta_y = e.getY();
		 temp_left_index = left_index
		 + (int) Math.floor((delta_x / division) * scale);
		 temp_lower_index = lower_index
		 + (int) Math.floor(((800 - delta_y) / division) * scale);
		
	}

	//
	@Override
	public void mouseReleased(MouseEvent e) {
		 // // TODO Auto-generated method stub
		 int delta_x = e.getX();
		 int delta_y = e.getY();
		 temp_right_index = left_index
		 + (int) Math.ceil((delta_x / division) * scale);
		 temp_upper_index = lower_index
		 + (int) Math.ceil(((800 - delta_y) / division) * scale);
		 // System.out.println(temp_right_index);
		 if (temp_right_index < temp_left_index) {// swap the limits
		 double temp = temp_right_index;
		 temp_right_index = temp_left_index;
		 temp_left_index = temp;
		 }
		 if (temp_upper_index < temp_lower_index) {// swap the limits
		 double temp = temp_lower_index;
		 temp_lower_index = temp_upper_index;
		 temp_upper_index = temp;
		 }
		 if (temp_right_index - temp_left_index > 4) {
		 System.out.println(temp_left_index + " , " + temp_right_index
		 + " : " + temp_lower_index + " , " + temp_upper_index);
		 left_index = temp_left_index;
		 right_index = temp_right_index;
		 lower_index = temp_lower_index;
		 upper_index = lower_index + num_of_divisions;
		
		 scale = ((double)(right_index - left_index)) / num_of_divisions;
		
		 repaint();
		 }
	}

	@Override
	public void mouseDragged(MouseEvent e) {

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		sphere_x = e.getX();
		repaint();
	}

}
