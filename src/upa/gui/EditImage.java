package upa.gui;

import upa.db.entity.Image;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.SQLException;

public class EditImage extends JDialog
{
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JButton rotateMinus90Button;
    private JButton rotate90Button;
    private JButton mirrorHorizontallyButton;
    private JButton mirrorVerticallyButton;
    private JPanel imageDisplayPane;

    private Image image;
    private BufferedImage imageToBeDisplayed;
    private BufferedImage imageToBeDisplayedFiltered;

    private boolean mirrorHorizontal = false;
    private boolean mirrorVertical = false;


    //=====================================================================dd==
    // CONSTRUCTORS
    //=====================================================================dd==

    public EditImage(Image image)
    {
        //-----------------------------------------------------dd--
        //  Window setup
        //-----------------------------------------------------dd--

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);


        //-----------------------------------------------------dd--
        //  Data setup
        //-----------------------------------------------------dd--

        this.image = image;

        JPanel imageDisplay = new JPanel()
        {
            @Override
            protected void paintComponent(Graphics g)
            {
                super.paintComponent(g);
                Dimension parentSize = getParent().getSize();
                try
                {
                    BufferedImage imageData = ImageIO.read(image.image.getDataInStream());
                    float widthResizeRatio = (float) parentSize.getWidth() / (float) imageData.getWidth();
                    float heightResizeRatio = (float) parentSize.getHeight() / (float) imageData.getHeight();
                    float resizeRatio = Math.min(Math.min(heightResizeRatio, widthResizeRatio), 1);

                    int width = (int) (imageData.getWidth() * resizeRatio);
                    int height = (int) (imageData.getHeight() * resizeRatio);
                    BufferedImage imageAfter = new BufferedImage(width, height, imageData.getType());

                    AffineTransform transform = new AffineTransform();
                    transform.scale(resizeRatio, resizeRatio);

                    AffineTransformOp transformOp = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
                    transformOp.filter(imageData, imageAfter);
                    imageToBeDisplayedFiltered = imageAfter;
                }
                catch (IOException | SQLException e)
                {
                    e.printStackTrace();
                }

                g.drawImage(imageToBeDisplayedFiltered, 0, 0, null);
                setPreferredSize(new Dimension(imageToBeDisplayedFiltered.getWidth(), imageToBeDisplayedFiltered.getHeight()));
            }
        };

        try
        {
            imageToBeDisplayed = ImageIO.read(image.image.getDataInStream());
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            screenSize.width = screenSize.width - 200;
            screenSize.height = screenSize.height - 200;
            Dimension imageSize = new Dimension(imageToBeDisplayed.getWidth(), imageToBeDisplayed.getHeight());

            imageSize.width = Math.min(screenSize.width, imageSize.width);
            imageSize.height = Math.min(screenSize.height, imageSize.height);

            float widthResizeRatio = (float) imageSize.width / (float) imageToBeDisplayed.getWidth();
            float heightResizeRatio = (float) imageSize.height / (float) imageToBeDisplayed.getHeight();
            if (widthResizeRatio < heightResizeRatio)
            {
                imageSize.width = (int) (imageSize.width * widthResizeRatio);
                imageSize.height = (int) (imageSize.height * heightResizeRatio);
            }
            else
            {
                imageSize.width = (int) (imageSize.width * heightResizeRatio);
                imageSize.height = (int) (imageSize.height * widthResizeRatio);
            }

            imageDisplayPane.add(imageDisplay);
            imageDisplay.setPreferredSize(imageSize);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


        //-----------------------------------------------------dd--
        //  Listeners setup
        //-----------------------------------------------------dd--

        rotate90Button.addActionListener(e -> ProcessRotation(90));
        rotateMinus90Button.addActionListener(e -> ProcessRotation(-90));

        mirrorHorizontallyButton.addActionListener(e -> ProcessMirror());
        mirrorVerticallyButton.addActionListener(e -> ProcessFlip());

        buttonOK.addActionListener(e -> onOK());
        buttonCancel.addActionListener(e -> onCancel());
    }

    private void ProcessRotation(final int rotation)
    {
        ProcessOracleMultimediaMethod("rotate=" + rotation);
    }

    private void ProcessMirror()
    {
        ProcessOracleMultimediaMethod("mirror");
    }

    private void ProcessFlip()
    {
        ProcessOracleMultimediaMethod("flip");
    }

    private void ProcessOracleMultimediaMethod(final String method)
    {
        try
        {
            image.PrepareForUpdate();
            image.image.process(method);
            image.CommitUpdate();
            imageToBeDisplayed = ImageIO.read(image.image.getDataInStream());
            repaint();
        }
        catch (SQLException | IOException ex)
        {
            ex.printStackTrace();
        }
    }


    //=====================================================================dd==
    // OTHER ACTION METHODS
    //=====================================================================dd==

    private void onOK()
    {
        dispose();
    }

    private void onCancel()
    {
        dispose();
    }
}
